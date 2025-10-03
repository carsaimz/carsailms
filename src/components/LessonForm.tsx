import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Lesson } from "@/hooks/useLessonsManagement";

interface LessonFormProps {
  lesson?: Lesson;
  courseId: string;
  nextOrderIndex: number;
  onSubmit: (data: any) => Promise<void>;
  onCancel?: () => void;
}

export function LessonForm({ lesson, courseId, nextOrderIndex, onSubmit, onCancel }: LessonFormProps) {
  const [formData, setFormData] = useState({
    title: lesson?.title || "",
    description: lesson?.description || "",
    content_type: lesson?.content_type || "video" as 'video' | 'pdf' | 'text',
    text_content: lesson?.text_content || "",
    order_index: lesson?.order_index || nextOrderIndex,
    duration_minutes: lesson?.duration_minutes || 0,
    is_preview: lesson?.is_preview || false,
    course_id: courseId,
  });

  const [contentFile, setContentFile] = useState<File | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      await onSubmit({ ...formData, contentFile });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="space-y-2">
        <Label htmlFor="title">Título da Lição *</Label>
        <Input
          id="title"
          value={formData.title}
          onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          required
          placeholder="Ex: Introdução aos Componentes"
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="description">Descrição</Label>
        <Textarea
          id="description"
          value={formData.description}
          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          rows={3}
          placeholder="Breve descrição do conteúdo desta lição..."
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="order">Ordem</Label>
          <Input
            id="order"
            type="number"
            min="0"
            value={formData.order_index}
            onChange={(e) => setFormData({ ...formData, order_index: parseInt(e.target.value) })}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="duration">Duração (minutos)</Label>
          <Input
            id="duration"
            type="number"
            min="0"
            value={formData.duration_minutes}
            onChange={(e) => setFormData({ ...formData, duration_minutes: parseInt(e.target.value) })}
          />
        </div>
      </div>

      <div className="space-y-2">
        <Label>Tipo de Conteúdo *</Label>
        <Tabs value={formData.content_type} onValueChange={(value: any) => setFormData({ ...formData, content_type: value })}>
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="video">Vídeo</TabsTrigger>
            <TabsTrigger value="pdf">PDF</TabsTrigger>
            <TabsTrigger value="text">Texto</TabsTrigger>
          </TabsList>

          <TabsContent value="video" className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="video-file">Upload de Vídeo</Label>
              <Input
                id="video-file"
                type="file"
                accept="video/*"
                onChange={(e) => setContentFile(e.target.files?.[0] || null)}
              />
              <p className="text-sm text-muted-foreground">
                Formatos aceitos: MP4, WebM, OGG
              </p>
            </div>
          </TabsContent>

          <TabsContent value="pdf" className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="pdf-file">Upload de PDF</Label>
              <Input
                id="pdf-file"
                type="file"
                accept=".pdf"
                onChange={(e) => setContentFile(e.target.files?.[0] || null)}
              />
            </div>
          </TabsContent>

          <TabsContent value="text" className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="text-content">Conteúdo em Texto</Label>
              <Textarea
                id="text-content"
                value={formData.text_content}
                onChange={(e) => setFormData({ ...formData, text_content: e.target.value })}
                rows={10}
                placeholder="Digite o conteúdo da lição aqui..."
              />
            </div>
          </TabsContent>
        </Tabs>
      </div>

      <div className="flex items-center space-x-2">
        <Switch
          id="preview"
          checked={formData.is_preview}
          onCheckedChange={(checked) => setFormData({ ...formData, is_preview: checked })}
        />
        <Label htmlFor="preview">Lição de pré-visualização (acessível sem inscrição)</Label>
      </div>

      <div className="flex gap-4">
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Salvando..." : lesson ? "Atualizar Lição" : "Criar Lição"}
        </Button>
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancelar
          </Button>
        )}
      </div>
    </form>
  );
}
