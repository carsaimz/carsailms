import { useState, useEffect } from 'react';
import { collection, query, orderBy, limit, where, getDocs, DocumentData } from 'firebase/firestore';
import { db } from '@/lib/firebase';
import { Course } from '@/types';

export const useCourses = (featured?: boolean, limitCount?: number) => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        let q = query(collection(db, 'courses'), orderBy('createdAt', 'desc'));

        if (featured !== undefined) {
          q = query(q, where('featured', '==', featured));
        }

        if (limitCount) {
          q = query(q, limit(limitCount));
        }

        const snapshot = await getDocs(q);
        const coursesData = snapshot.docs.map((doc) => {
          const data = doc.data();
          return {
            id: doc.id,
            ...data,
            createdAt: data.createdAt?.toDate() || new Date(),
          } as Course;
        });

        setCourses(coursesData);
      } catch (err) {
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, [featured, limitCount]);

  return { courses, loading, error };
};
