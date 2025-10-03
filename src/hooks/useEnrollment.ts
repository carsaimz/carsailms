import { useState, useEffect } from "react";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";

export interface Enrollment {
  id: string;
  user_id: string;
  course_id: string;
  enrolled_at: string;
  completed: boolean;
  completed_at: string | null;
  progress_percentage: number;
}

export interface LessonProgress {
  id: string;
  enrollment_id: string;
  lesson_id: string;
  completed: boolean;
  completed_at: string | null;
  last_position: number;
}

export function useEnrollment(userId: string, courseId?: string) {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [currentEnrollment, setCurrentEnrollment] = useState<Enrollment | null>(null);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  // Check if user is enrolled in a course
  const checkEnrollment = async (checkCourseId: string) => {
    const { data } = await supabase
      .from("enrollments")
      .select("*")
      .eq("user_id", userId)
      .eq("course_id", checkCourseId)
      .maybeSingle();

    return data;
  };

  // Fetch all enrollments for user
  const fetchEnrollments = async () => {
    setLoading(true);
    const { data, error } = await supabase
      .from("enrollments")
      .select("*")
      .eq("user_id", userId)
      .order("enrolled_at", { ascending: false });

    if (error) {
      toast({
        title: "Erro ao carregar inscrições",
        description: error.message,
        variant: "destructive",
      });
      setLoading(false);
      return;
    }

    setEnrollments(data || []);
    setLoading(false);
  };

  // Fetch specific enrollment
  const fetchCurrentEnrollment = async () => {
    if (!courseId) return;

    const enrollment = await checkEnrollment(courseId);
    setCurrentEnrollment(enrollment);
  };

  // Enroll in course
  const enrollInCourse = async (enrollCourseId: string) => {
    const existing = await checkEnrollment(enrollCourseId);

    if (existing) {
      toast({
        title: "Já inscrito",
        description: "Você já está inscrito neste curso.",
      });
      return existing;
    }

    const { data, error } = await supabase
      .from("enrollments")
      .insert([{
        user_id: userId,
        course_id: enrollCourseId,
        progress_percentage: 0,
      }])
      .select()
      .single();

    if (error) {
      toast({
        title: "Erro ao inscrever",
        description: error.message,
        variant: "destructive",
      });
      return null;
    }

    toast({
      title: "Inscrição realizada!",
      description: "Você foi inscrito no curso com sucesso.",
    });

    fetchEnrollments();
    return data;
  };

  // Mark lesson as completed
  const markLessonComplete = async (enrollmentId: string, lessonId: string, position = 0) => {
    // Check if progress record exists
    const { data: existing } = await supabase
      .from("lesson_progress")
      .select("*")
      .eq("enrollment_id", enrollmentId)
      .eq("lesson_id", lessonId)
      .maybeSingle();

    if (existing) {
      // Update existing
      const { error } = await supabase
        .from("lesson_progress")
        .update({
          completed: true,
          completed_at: new Date().toISOString(),
          last_position: position,
        })
        .eq("id", existing.id);

      if (error) {
        toast({
          title: "Erro ao atualizar progresso",
          description: error.message,
          variant: "destructive",
        });
        return false;
      }
    } else {
      // Create new
      const { error } = await supabase
        .from("lesson_progress")
        .insert([{
          enrollment_id: enrollmentId,
          lesson_id: lessonId,
          completed: true,
          completed_at: new Date().toISOString(),
          last_position: position,
        }]);

      if (error) {
        toast({
          title: "Erro ao salvar progresso",
          description: error.message,
          variant: "destructive",
        });
        return false;
      }
    }

    // Update overall progress
    await updateCourseProgress(enrollmentId);
    return true;
  };

  // Update course progress percentage
  const updateCourseProgress = async (enrollmentId: string) => {
    // Get total lessons and completed lessons
    const { data: enrollment } = await supabase
      .from("enrollments")
      .select("course_id")
      .eq("id", enrollmentId)
      .single();

    if (!enrollment) return;

    const { data: lessons } = await supabase
      .from("lessons")
      .select("id")
      .eq("course_id", enrollment.course_id);

    const { data: progress } = await supabase
      .from("lesson_progress")
      .select("id")
      .eq("enrollment_id", enrollmentId)
      .eq("completed", true);

    const totalLessons = lessons?.length || 0;
    const completedLessons = progress?.length || 0;
    const percentage = totalLessons > 0 ? Math.round((completedLessons / totalLessons) * 100) : 0;

    await supabase
      .from("enrollments")
      .update({
        progress_percentage: percentage,
        completed: percentage === 100,
        completed_at: percentage === 100 ? new Date().toISOString() : null,
      })
      .eq("id", enrollmentId);

    fetchCurrentEnrollment();
  };

  // Get lesson progress
  const getLessonProgress = async (enrollmentId: string, lessonId: string) => {
    const { data } = await supabase
      .from("lesson_progress")
      .select("*")
      .eq("enrollment_id", enrollmentId)
      .eq("lesson_id", lessonId)
      .maybeSingle();

    return data;
  };

  useEffect(() => {
    if (userId) {
      fetchEnrollments();
      if (courseId) {
        fetchCurrentEnrollment();
      }
    }
  }, [userId, courseId]);

  return {
    enrollments,
    currentEnrollment,
    loading,
    enrollInCourse,
    markLessonComplete,
    getLessonProgress,
    refetch: fetchEnrollments,
    isEnrolled: !!currentEnrollment,
  };
}
