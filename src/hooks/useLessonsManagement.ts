import { useState, useEffect } from "react";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";

export interface Lesson {
  id: string;
  course_id: string;
  title: string;
  description: string | null;
  content_type: 'video' | 'pdf' | 'text';
  content_url: string | null;
  text_content: string | null;
  order_index: number;
  duration_minutes: number;
  is_preview: boolean;
  created_at: string;
  updated_at: string;
}

export function useLessonsManagement(courseId: string) {
  const [lessons, setLessons] = useState<Lesson[]>([]);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  // Fetch lessons
  const fetchLessons = async () => {
    setLoading(true);
    const { data, error } = await supabase
      .from("lessons")
      .select("*")
      .eq("course_id", courseId)
      .order("order_index");

    if (error) {
      toast({
        title: "Erro ao carregar lições",
        description: error.message,
        variant: "destructive",
      });
      setLoading(false);
      return;
    }

    setLessons(data || []);
    setLoading(false);
  };

  // Create lesson
  const createLesson = async (lessonData: Omit<Lesson, 'id' | 'created_at' | 'updated_at'>) => {
    const { data, error } = await supabase
      .from("lessons")
      .insert([lessonData])
      .select()
      .single();

    if (error) {
      toast({
        title: "Erro ao criar lição",
        description: error.message,
        variant: "destructive",
      });
      return null;
    }

    toast({
      title: "Lição criada!",
      description: "A lição foi adicionada com sucesso.",
    });

    fetchLessons();
    return data;
  };

  // Update lesson
  const updateLesson = async (id: string, updates: Partial<Lesson>) => {
    const { error } = await supabase
      .from("lessons")
      .update(updates)
      .eq("id", id);

    if (error) {
      toast({
        title: "Erro ao atualizar lição",
        description: error.message,
        variant: "destructive",
      });
      return false;
    }

    toast({
      title: "Lição atualizada!",
      description: "As alterações foram salvas.",
    });

    fetchLessons();
    return true;
  };

  // Delete lesson
  const deleteLesson = async (id: string) => {
    const { error } = await supabase
      .from("lessons")
      .delete()
      .eq("id", id);

    if (error) {
      toast({
        title: "Erro ao deletar lição",
        description: error.message,
        variant: "destructive",
      });
      return false;
    }

    toast({
      title: "Lição deletada!",
      description: "A lição foi removida com sucesso.",
    });

    fetchLessons();
    return true;
  };

  // Upload video
  const uploadVideo = async (file: File, lessonId: string) => {
    const fileExt = file.name.split('.').pop();
    const fileName = `${lessonId}-${Date.now()}.${fileExt}`;

    const { error: uploadError } = await supabase.storage
      .from("lesson-videos")
      .upload(fileName, file);

    if (uploadError) {
      toast({
        title: "Erro ao fazer upload do vídeo",
        description: uploadError.message,
        variant: "destructive",
      });
      return null;
    }

    return fileName;
  };

  // Upload PDF
  const uploadPDF = async (file: File, lessonId: string) => {
    const fileExt = file.name.split('.').pop();
    const fileName = `${lessonId}-${Date.now()}.${fileExt}`;

    const { error: uploadError } = await supabase.storage
      .from("lesson-pdfs")
      .upload(fileName, file);

    if (uploadError) {
      toast({
        title: "Erro ao fazer upload do PDF",
        description: uploadError.message,
        variant: "destructive",
      });
      return null;
    }

    return fileName;
  };

  // Reorder lessons
  const reorderLessons = async (newOrder: { id: string; order_index: number }[]) => {
    const updates = newOrder.map(item =>
      supabase.from("lessons").update({ order_index: item.order_index }).eq("id", item.id)
    );

    const results = await Promise.all(updates);
    const errors = results.filter(r => r.error);

    if (errors.length > 0) {
      toast({
        title: "Erro ao reordenar lições",
        variant: "destructive",
      });
      return false;
    }

    fetchLessons();
    return true;
  };

  useEffect(() => {
    if (courseId) {
      fetchLessons();
    }
  }, [courseId]);

  return {
    lessons,
    loading,
    createLesson,
    updateLesson,
    deleteLesson,
    uploadVideo,
    uploadPDF,
    reorderLessons,
    refetch: fetchLessons,
  };
}
