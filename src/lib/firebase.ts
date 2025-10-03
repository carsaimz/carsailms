import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';
import { getStorage } from 'firebase/storage';

const firebaseConfig = {
  apiKey: "AIzaSyDZn_OOjK9bufC8eCw2ejIgQBcDM1dn3x4",
  authDomain: "carsai-lms.firebaseapp.com",
  projectId: "carsai-lms",
  storageBucket: "carsai-lms.firebasestorage.app",
  messagingSenderId: "33259620207",
  appId: "1:33259620207:web:347bc804685f29ff6e0a9a",
  measurementId: "G-E8S6E7HYPS"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase services
export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);

export default app;
