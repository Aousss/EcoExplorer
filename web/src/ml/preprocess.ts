// Must match how the model was trained (fastai notebook) and tools/convert_model.py:
// centre-crop to a square, resize to 224×224, scale to 0–1, ImageNet mean/std, NCHW float32.
export const IMAGE_SIZE = 224
const MEAN = [0.485, 0.456, 0.406]
const STD = [0.229, 0.224, 0.225]

export async function imageToTensorData(image: Blob): Promise<Float32Array> {
  const source = await createImageBitmap(image) // applies EXIF orientation
  const side = Math.min(source.width, source.height)
  const resized = await createImageBitmap(
    source,
    (source.width - side) / 2,
    (source.height - side) / 2,
    side,
    side,
    { resizeWidth: IMAGE_SIZE, resizeHeight: IMAGE_SIZE, resizeQuality: 'high' },
  )
  source.close()

  const canvas = new OffscreenCanvas(IMAGE_SIZE, IMAGE_SIZE)
  const context = canvas.getContext('2d')!
  context.drawImage(resized, 0, 0)
  resized.close()
  const { data: rgba } = context.getImageData(0, 0, IMAGE_SIZE, IMAGE_SIZE)

  const pixels = IMAGE_SIZE * IMAGE_SIZE
  const tensor = new Float32Array(3 * pixels)
  for (let i = 0; i < pixels; i++) {
    for (let c = 0; c < 3; c++) {
      tensor[c * pixels + i] = (rgba[i * 4 + c] / 255 - MEAN[c]) / STD[c]
    }
  }
  return tensor
}

export function softmax(logits: ArrayLike<number>): number[] {
  const max = Math.max(...Array.from(logits))
  const exps = Array.from(logits, (x) => Math.exp(x - max))
  const sum = exps.reduce((a, b) => a + b, 0)
  return exps.map((e) => e / sum)
}
