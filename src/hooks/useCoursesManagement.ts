import { useState, useEffect } from "react";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";

export interface Course {
  id: string;
  title: string;
  description: string | null;
  instructor_id: string;
  category_id: string | null;
  price: number;
  level: 'beginner' | 'intermediate' | 'advanced';
  thumbnail_url: string | null;
  published: boolean;
  featured: boolean;
  duration_hours: number;
  created_at: string;
  updated_at: string;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  description: string | null;
}

export function useCoursesManagement(userId?: string) {
  const [courses, setCourses] = useState<Course[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  // Fetch categories
  const fetchCategories = async () => {
    const { data, error } = await supabase
      .from("categories")
      .select("*")
      .order("name");

    if (error) {
      toast({
        title: "Erro ao carregar categorias",
        description: error.message,
        variant: "destructive",
      });
      return;
    }

    setCategories(data || []);
  };

  // Fetch courses
  const fetchCourses = async () => {
    setLoading(true);
    let query = supabase.from("courses").select("*").order("created_at", { ascending: false });

    // If userId is provided, filter by instructor
    if (userId) {
      query = query.eq("instructor_id", userId);
    }

    const { data, error } = await query;

    if (error) {
      toast({
        title: "Erro ao carregar cursos",
        description: error.message,
        variant: "destructive",
      });
      setLoading(false);
      return;
    }

    setCourses(data || []);
    setLoading(false);
  };

  // Create course
  const createCourse = async (courseData: Omit<Course, 'id' | 'created_at' | 'updated_at'>) => {
    const { data, error } = await supabase
      .from("courses")
      .insert([courseData])
      .select()
      .single();

    if (error) {
      toast({
        title: "Erro ao criar curso",
        description: error.message,
        variant: "destructive",
      });
      return null;
    }

    toast({
      title: "Curso criado!",
      description: "O curso foi criado com sucesso.",
    });

    fetchCourses();
    return data;
  };

  // Update course
  const updateCourse = async (id: string, updates: Partial<Course>) => {
    const { error } = await supabase
      .from("courses")
      .update(updates)
      .eq("id", id);

    if (error) {
      toast({
        title: "Erro ao atualizar curso",
        description: error.message,
        variant: "destructive",
      });
      return false;
    }

    toast({
      title: "Curso atualizado!",
      description: "As alterações foram salvas.",
    });

    fetchCourses();
    return true;
  };

  // Delete course
  const deleteCourse = async (id: string) => {
    const { error } = await supabase
      .from("courses")
      .delete()
      .eq("id", id);

    if (error) {
      toast({
        title: "Erro ao deletar curso",
        description: error.message,
        variant: "destructive",
      });
      return false;
    }

    toast({
      title: "Curso deletado!",
      description: "O curso foi removido com sucesso.",
    });

    fetchCourses();
    return true;
  };

  // Upload thumbnail
  const uploadThumbnail = async (file: File, courseId: string) => {
    const fileExt = file.name.split('.').pop();
    const fileName = `${courseId}-${Date.now()}.${fileExt}`;

    const { error: uploadError } = await supabase.storage
      .from("course-thumbnails")
      .upload(fileName, file);

    if (uploadError) {
      toast({
        title: "Erro ao fazer upload",
        description: uploadError.message,
        variant: "destructive",
      });
      return null;
    }

    const { data: { publicUrl } } = supabase.storage
      .from("course-thumbnails")
      .getPublicUrl(fileName);

    return publicUrl;
  };

  useEffect(() => {
    fetchCategories();
    fetchCourses();
  }, [userId]);

  return {
    courses,
    categories,
    loading,
    createCourse,
    updateCourse,
    deleteCourse,
    uploadThumbnail,
    refetch: fetchCourses,
  };
}
