"""
Shrink tools/models/model.onnx (float32, 47 MB) to the 8-bit web/public/models/model.int8.onnx (12 MB),
then check it gives the same predictions as the float32 model.

Dynamic quantization stores the weights as uint8 and needs no calibration images.
The float32 model must exist first (run convert_model.py).

Usage (from the repo root):
    tools\.venv\Scripts\python.exe tools\quantize_model.py
"""
import tempfile
from pathlib import Path

import numpy as np
import onnxruntime as ort
from onnxruntime.quantization import QuantType, quant_pre_process, quantize_dynamic

from convert_model import EXPECTED, LABELS, MODEL_ONNX, ROOT, TEST_IMAGES, preprocess, softmax

MODEL_INT8 = ROOT / "web/public/models/model.int8.onnx"
# The web app's species photos are a second test set
SPECIES_IMAGES = ROOT / "web/public/images/species"


def quantize() -> None:
    with tempfile.TemporaryDirectory() as tmp:
        prepared = Path(tmp) / "prepared.onnx"
        quant_pre_process(str(MODEL_ONNX), str(prepared))
        # uint8 weights: ConvInteger in onnxruntime only supports uint8 x uint8
        quantize_dynamic(str(prepared), str(MODEL_INT8), weight_type=QuantType.QUInt8)


def verify() -> bool:
    fp32 = ort.InferenceSession(str(MODEL_ONNX), providers=["CPUExecutionProvider"])
    int8 = ort.InferenceSession(str(MODEL_INT8), providers=["CPUExecutionProvider"])
    images = sorted(p for p in TEST_IMAGES.iterdir() if p.stem in EXPECTED)
    images += sorted(SPECIES_IMAGES.glob("*.jpg"))

    print(f"\n{'image':<26} {'float32':<18} {'prob':>6}  {'int8':<18} {'prob':>6}  same")
    all_match, max_prob_diff = True, 0.0
    for path in images:
        x = preprocess(path)
        p32 = softmax(fp32.run(None, {"input": x})[0][0])
        p8 = softmax(int8.run(None, {"input": x})[0][0])
        i32, i8 = int(p32.argmax()), int(p8.argmax())
        same = i32 == i8
        all_match &= same
        max_prob_diff = max(max_prob_diff, float(np.abs(p32 - p8).max()))
        print(f"{path.name:<26} {LABELS[i32]:<18} {p32[i32]:>6.3f}  {LABELS[i8]:<18} {p8[i8]:>6.3f}  "
              f"{'yes' if same else 'NO'}")

    print(f"\nfloat32 vs int8 on {len(images)} images: top-1 identical = {all_match}, "
          f"max probability difference = {max_prob_diff:.3f}")
    return all_match


def main() -> None:
    quantize()
    print(f"Quantized {MODEL_INT8.relative_to(ROOT)} ({MODEL_INT8.stat().st_size / 1e6:.1f} MB, "
          f"was {MODEL_ONNX.stat().st_size / 1e6:.1f} MB)")
    ok = verify()
    print("VERIFIED: int8 model matches float32" if ok else "MISMATCH: check the output above")


if __name__ == "__main__":
    main()
