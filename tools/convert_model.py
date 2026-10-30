"""
Convert the Android TorchScript model (model.pt) to ONNX (tools/models/model.onnx),
then verify the ONNX model gives the same predictions as the original.

Usage (from the repo root):
    tools\.venv\Scripts\python.exe tools\convert_model.py

Then run quantize_model.py to make the web app's model.
"""
from pathlib import Path

import numpy as np
import onnxruntime as ort
import torch
from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
MODEL_PT = ROOT / "app/src/main/assets/model.pt"
# Full-precision model; quantize_model.py makes the smaller one the web app loads
OUT_DIR = ROOT / "tools/models"
MODEL_ONNX = OUT_DIR / "model.onnx"
TEST_IMAGES = ROOT / "app/src/main/res/drawable"

# Same order as fastai dls.vocab (alphabetical) and CLASS_LABELS in IdentifyFragment.java
LABELS = ["Butterfly", "Cicadas", "Coconut", "Daisy", "Damselfly", "Dragonfly", "Leafhopper",
          "Longhorn Beetle", "Moths", "Oil Palm", "Pinang", "Rhinoceros Beetle", "Sunflower",
          "Torch Ginger", "Turmeric", "Weevil"]

# drawable file name -> expected label (rose/tulip are not in the 16 classes)
EXPECTED = {
    "identify_butterfly": "Butterfly", "identify_cicadas": "Cicadas", "identify_coconut": "Coconut",
    "identify_daisy": "Daisy", "identify_damselfly": "Damselfly", "identify_dragonfly": "Dragonfly",
    "identify_leafhopper": "Leafhopper", "identify_longhorn": "Longhorn Beetle", "identify_moths": "Moths",
    "identify_oilpalm": "Oil Palm", "identify_pinang": "Pinang", "identify_rhinoceros": "Rhinoceros Beetle",
    "identify_sunflower": "Sunflower", "identify_torchginger": "Torch Ginger", "identify_tumeric": "Turmeric",
    "identify_weevil": "Weevil", "sunflower": "Sunflower", "rose": None, "tulip": None,
}

IMAGE_SIZE = 224
MEAN = np.array([0.485, 0.456, 0.406], dtype=np.float32)
STD = np.array([0.229, 0.224, 0.225], dtype=np.float32)


def preprocess(path: Path) -> np.ndarray:
    """Centre-crop to square, resize to 224, ImageNet-normalise, NCHW float32.
    Matches fastai Resize(224) (default crop method) used in training."""
    img = Image.open(path).convert("RGB")
    w, h = img.size
    side = min(w, h)
    left, top = (w - side) // 2, (h - side) // 2
    img = img.crop((left, top, left + side, top + side)).resize((IMAGE_SIZE, IMAGE_SIZE), Image.BILINEAR)
    x = (np.asarray(img, dtype=np.float32) / 255.0 - MEAN) / STD
    return x.transpose(2, 0, 1)[None]


def softmax(logits: np.ndarray) -> np.ndarray:
    e = np.exp(logits - logits.max())
    return e / e.sum()


def export_onnx(model: torch.jit.ScriptModule) -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    dummy = torch.randn(1, 3, IMAGE_SIZE, IMAGE_SIZE)
    # dynamo=False: the model is a traced TorchScript module, which the dynamo exporter cannot handle
    torch.onnx.export(model, dummy, str(MODEL_ONNX), input_names=["input"], output_names=["logits"],
                      opset_version=17, dynamo=False)


def verify(model: torch.jit.ScriptModule) -> bool:
    session = ort.InferenceSession(str(MODEL_ONNX), providers=["CPUExecutionProvider"])
    images = sorted(p for p in TEST_IMAGES.iterdir() if p.stem in EXPECTED)

    print(f"\n{'image':<22} {'expected':<18} {'predicted':<18} {'prob':>6}  {'max |diff|':>10}  ok")
    all_match, max_diff = True, 0.0
    for path in images:
        x = preprocess(path)
        with torch.no_grad():
            torch_logits = model(torch.from_numpy(x)).numpy()[0]
        onnx_logits = session.run(None, {"input": x})[0][0]

        diff = float(np.abs(torch_logits - onnx_logits).max())
        max_diff = max(max_diff, diff)
        same_top1 = int(torch_logits.argmax()) == int(onnx_logits.argmax())
        all_match &= same_top1

        probs = softmax(onnx_logits)
        idx = int(probs.argmax())
        expected = EXPECTED[path.stem]
        correct = "-" if expected is None else ("yes" if LABELS[idx] == expected else "NO")
        print(f"{path.name:<22} {expected or '(not a class)':<18} {LABELS[idx]:<18} {probs[idx]:>6.3f}  "
              f"{diff:>10.2e}  {correct}")

    print(f"\nPyTorch vs ONNX: top-1 identical on all images = {all_match}, max logit difference = {max_diff:.2e}")
    return all_match and max_diff < 1e-3


def main() -> None:
    model = torch.jit.load(str(MODEL_PT), map_location="cpu").eval()
    export_onnx(model)
    print(f"Exported {MODEL_ONNX.relative_to(ROOT)} ({MODEL_ONNX.stat().st_size / 1e6:.1f} MB)")
    ok = verify(model)
    print("VERIFIED: ONNX model matches model.pt" if ok else "MISMATCH: check the output above")


if __name__ == "__main__":
    main()
