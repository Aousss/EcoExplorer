// funfacts/{id} → text, description?, imageUrl?, source? (same data the Android HomeFragment reads)
import { child, get, ref } from 'firebase/database'
import { db } from '../firebase'

export type FunFact = {
  id: string
  text: string
  description?: string
  imageUrl?: string
  source?: string
}

export async function fetchFunFacts(): Promise<FunFact[]> {
  const snapshot = await get(ref(db, 'funfacts'))
  const facts: FunFact[] = []
  snapshot.forEach((factSnapshot) => {
    const fact = factSnapshot.val()
    if (fact?.text) facts.push({ id: factSnapshot.key!, ...fact })
  })
  return facts
}

export async function fetchFunFact(id: string): Promise<FunFact | null> {
  const snapshot = await get(child(ref(db, 'funfacts'), id))
  return snapshot.exists() ? { id, ...snapshot.val() } : null
}

/** Random fact, avoiding the one currently shown when there is a choice */
export function pickRandom(facts: FunFact[], currentId?: string): FunFact | null {
  const choices = facts.length > 1 ? facts.filter((fact) => fact.id !== currentId) : facts
  return choices.length ? choices[Math.floor(Math.random() * choices.length)] : null
}
