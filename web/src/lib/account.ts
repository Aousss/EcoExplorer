// Account actions shared by the auth and profile pages.
// Data layout matches the Android app: users/{uid} → email, username, fullname, age, uid, createdAt, profileImageUrl
import {
  createUserWithEmailAndPassword,
  sendPasswordResetEmail,
  signInWithEmailAndPassword,
  signOut,
} from 'firebase/auth'
import { get, ref, set, type DatabaseReference } from 'firebase/database'
import { getDownloadURL, ref as storageRef, uploadBytes } from 'firebase/storage'
import { FirebaseError } from 'firebase/app'
import { auth, db, storage } from '../firebase'

export type UserProfile = {
  uid: string
  email: string
  username: string
  fullname: string
  age: number
  createdAt: number
  profileImageUrl?: string
}

export type RegisterInput = {
  username: string
  fullname: string
  age: number
  email: string
  password: string
}

export async function login(email: string, password: string) {
  await signInWithEmailAndPassword(auth, email, password)
  // The Android app also wrote the plain-text password to users/{uid}/password here; the web app does not.
}

export async function register({ username, fullname, age, email, password }: RegisterInput) {
  // usernames/{username} → uid is the uniqueness index. The Android app checked users/{username},
  // which never exists because users are keyed by uid.
  const usernameRef = ref(db, `usernames/${username.toLowerCase()}`)
  if ((await readIfAllowed(usernameRef))?.exists()) {
    throw new AccountError('That username is already taken.')
  }

  const { user } = await createUserWithEmailAndPassword(auth, email, password)
  const profile: UserProfile = { uid: user.uid, email, username, fullname, age, createdAt: Date.now() }
  await set(ref(db, `users/${user.uid}`), profile)
  await writeIfAllowed(usernameRef, user.uid)
}

export function resetPassword(email: string) {
  return sendPasswordResetEmail(auth, email)
}

export function logout() {
  return signOut(auth)
}

export async function uploadProfilePicture(uid: string, file: File) {
  // Same path as ProfileFragment.uploadImageToFirebase()
  const fileRef = storageRef(storage, `profile_pictures/${uid}.jpg`)
  await uploadBytes(fileRef, file, { contentType: file.type })
  const url = await getDownloadURL(fileRef)
  await set(ref(db, `users/${uid}/profileImageUrl`), url)
  return url
}

export class AccountError extends Error {}

/** Turns Firebase error codes into messages for the user */
export function errorMessage(error: unknown): string {
  if (error instanceof AccountError) return error.message
  if (error instanceof FirebaseError) {
    switch (error.code) {
      case 'auth/invalid-credential':
      case 'auth/wrong-password':
      case 'auth/user-not-found':
        return 'Incorrect email or password.'
      case 'auth/invalid-email':
        return 'Please enter a valid email address.'
      case 'auth/email-already-in-use':
        return 'An account with this email already exists.'
      case 'auth/weak-password':
        return 'Password must be at least 6 characters.'
      case 'auth/too-many-requests':
        return 'Too many attempts. Please wait a moment and try again.'
      case 'auth/network-request-failed':
        return 'No internet connection. Please check your network.'
      case 'storage/unauthorized':
        return 'You do not have permission to do that.'
    }
  }
  return 'Something went wrong. Please try again.'
}

// The usernames index is new. Until database.rules.json is deployed it may be unreadable or
// unwritable, so registration still works without it.
async function readIfAllowed(target: DatabaseReference) {
  try {
    return await get(target)
  } catch (error) {
    console.warn('usernames index not readable, skipping uniqueness check', error)
    return null
  }
}

async function writeIfAllowed(target: DatabaseReference, value: string) {
  try {
    await set(target, value)
  } catch (error) {
    console.warn('usernames index not writable', error)
  }
}
