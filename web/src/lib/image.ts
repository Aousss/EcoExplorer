/**
 * Centre-crops an image to a square, resizes it and returns a JPEG data URL.
 * Used for profile pictures, which are stored in the Realtime Database because
 * Firebase Storage needs the paid Blaze plan.
 */
export async function toSquareJpegDataUrl(file: Blob, size = 256, quality = 0.8): Promise<string> {
  const bitmap = await createImageBitmap(file) // applies EXIF orientation, so phone photos aren't rotated
  const side = Math.min(bitmap.width, bitmap.height)
  const canvas = document.createElement('canvas')
  canvas.width = canvas.height = size
  const context = canvas.getContext('2d')!
  context.drawImage(bitmap, (bitmap.width - side) / 2, (bitmap.height - side) / 2, side, side, 0, 0, size, size)
  bitmap.close()
  return canvas.toDataURL('image/jpeg', quality)
}
