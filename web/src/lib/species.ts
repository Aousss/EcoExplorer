// The 16 species the model recognises. species.json was generated from IdentifyFragment.java
// (plantList, animalsList and SPECIES_INFO, which were duplicates of each other).
import data from '../data/species.json'

export type Species = {
  id: string
  name: string
  category: 'plant' | 'animal'
  /** Output index in model.onnx (fastai dls.vocab order) */
  classIndex: number
  modelLabel: string
  description: string
  scientificName: string
  ecosystem: string
  habitat: string
  role: string
  image: string
}

export const SPECIES = data as Species[]

export const PLANTS = SPECIES.filter((s) => s.category === 'plant')
export const ANIMALS = SPECIES.filter((s) => s.category === 'animal')

export const getSpecies = (id: string) => SPECIES.find((s) => s.id === id)

const byClassIndex = new Map(SPECIES.map((s) => [s.classIndex, s]))
export const speciesForClass = (index: number) => byClassIndex.get(index)!
