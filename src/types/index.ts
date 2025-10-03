export type UserRole = 'admin' | 'instructor' | 'student';

export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  avatar?: string;
  createdAt: Date;
}

export interface Course {
  id: string;
  title: string;
  description: string;
  thumbnail: string;
  instructorId: string;
  instructorName: string;
  category: string;
  level: 'beginner' | 'intermediate' | 'advanced';
  price: number;
  featured: boolean;
  students: number;
  rating: number;
  reviews: number;
  createdAt: Date;
}

export interface Lesson {
  id: string;
  courseId: string;
  title: string;
  description: string;
  type: 'video' | 'text' | 'pdf';
  content: string; // URL for video/pdf, or text content
  duration?: number; // in minutes
  order: number;
  createdAt: Date;
}

export interface Enrollment {
  id: string;
  userId: string;
  courseId: string;
  progress: number; // 0-100
  enrolledAt: Date;
  completedAt?: Date;
}

export interface Payment {
  id: string;
  userId: string;
  courseId: string;
  amount: number;
  method: 'mpesa' | 'emola' | 'mkesh' | 'paypal';
  status: 'pending' | 'approved' | 'rejected';
  proofUrl?: string;
  createdAt: Date;
  processedAt?: Date;
}

export interface Review {
  id: string;
  userId: string;
  userName: string;
  courseId: string;
  rating: number;
  comment: string;
  createdAt: Date;
}

export interface Notification {
  id: string;
  userId: string;
  title: string;
  message: string;
  read: boolean;
  type: 'info' | 'success' | 'warning' | 'error';
  createdAt: Date;
}

export interface Post {
  id: string;
  title: string;
  content: string;
  excerpt: string;
  thumbnail: string;
  authorId: string;
  authorName: string;
  category: string;
  published: boolean;
  createdAt: Date;
}
