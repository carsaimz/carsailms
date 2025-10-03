import React, { useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { useCoursesManagement } from "@/hooks/useCoursesManagement";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { CourseForm } from "@/components/CourseForm";
import { Badge } from "@/components/ui/badge";
import { Plus, Edit, Trash2, Eye, BookOpen } from "lucide-react";
import { Link } from "react-router-dom";

export default function AdminCourses() {
  const { user } = useAuth();
  const { courses, categories, loading, createCourse, updateCourse, deleteCourse, uploadThumbnail } = useCoursesManagement();
  const [selectedCourse, setSelectedCourse] = useState<any>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const handleCreateCourse = async (data: any) => {
    const { thumbnailFile, ...courseData } = data;
    
    const newCourse = await createCourse(courseData);
    
    if (newCourse && thumbnailFile) {
      const thumbnailUrl = await uploadThumbnail(thumbnailFile, newCourse.id);
      if (thumbnailUrl) {
        await updateCourse(newCourse.id, { thumbnail_url: thumbnailUrl });
      }
    }

    setIsDialogOpen(false);
    setSelectedCourse(null);
  };

  const handleUpdateCourse = async (data: any) => {
    if (!selectedCourse) return;

    const { thumbnailFile, ...courseData } = data;

    if (thumbnailFile) {
      const thumbnailUrl = await uploadThumbnail(thumbnailFile, selectedCourse.id);
      if (thumbnailUrl) {
        courseData.thumbnail_url = thumbnailUrl;
      }
    }

    await updateCourse(selectedCourse.id, courseData);
    setIsDialogOpen(false);
    setSelectedCourse(null);
  };

  const handleDelete = async (courseId: string) => {
    if (confirm("Tem certeza que deseja deletar este curso? Esta ação não pode ser desfeita.")) {
      await deleteCourse(courseId);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <p>Carregando cursos...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold">Gestão de Cursos</h1>
            <p className="text-muted-foreground">Crie e gerencie todos os cursos da plataforma</p>
          </div>

          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button onClick={() => setSelectedCourse(null)}>
                <Plus className="w-4 h-4 mr-2" />
                Novo Curso
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>
                  {selectedCourse ? "Editar Curso" : "Novo Curso"}
                </DialogTitle>
              </DialogHeader>
              <CourseForm
                course={selectedCourse}
                categories={categories}
                userId={user?.uid || ""}
                onSubmit={selectedCourse ? handleUpdateCourse : handleCreateCourse}
                onCancel={() => {
                  setIsDialogOpen(false);
                  setSelectedCourse(null);
                }}
              />
            </DialogContent>
          </Dialog>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {courses.map((course) => (
            <Card key={course.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                {course.thumbnail_url && (
                  <img
                    src={course.thumbnail_url}
                    alt={course.title}
                    className="w-full h-48 object-cover rounded-md mb-4"
                  />
                )}
                <CardTitle className="line-clamp-2">{course.title}</CardTitle>
                <CardDescription className="line-clamp-2">
                  {course.description}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex flex-wrap gap-2 mb-4">
                  <Badge variant={course.published ? "default" : "secondary"}>
                    {course.published ? "Publicado" : "Rascunho"}
                  </Badge>
                  {course.featured && <Badge variant="destructive">Destaque</Badge>}
                  <Badge variant="outline">{course.level === 'beginner' ? 'Iniciante' : course.level === 'intermediate' ? 'Intermédio' : 'Avançado'}</Badge>
                </div>

                <div className="text-sm text-muted-foreground mb-4">
                  <p>Preço: {course.price > 0 ? `${course.price} MZN` : "Gratuito"}</p>
                  <p>Duração: {course.duration_hours}h</p>
                </div>

                <div className="flex gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setSelectedCourse(course);
                      setIsDialogOpen(true);
                    }}
                  >
                    <Edit className="w-4 h-4" />
                  </Button>

                  <Button
                    size="sm"
                    variant="outline"
                    asChild
                  >
                    <Link to={`/admin/courses/${course.id}/lessons`}>
                      <BookOpen className="w-4 h-4 mr-2" />
                      Lições
                    </Link>
                  </Button>

                  <Button
                    size="sm"
                    variant="outline"
                    asChild
                  >
                    <Link to={`/courses/${course.id}`}>
                      <Eye className="w-4 h-4" />
                    </Link>
                  </Button>

                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => handleDelete(course.id)}
                  >
                    <Trash2 className="w-4 h-4" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {courses.length === 0 && (
          <Card className="text-center py-12">
            <CardContent>
              <p className="text-muted-foreground mb-4">
                Nenhum curso criado ainda. Comece criando seu primeiro curso!
              </p>
              <Button onClick={() => setIsDialogOpen(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Criar Primeiro Curso
              </Button>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
