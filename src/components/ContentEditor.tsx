import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RichTextEditor } from "./RichTextEditor";
import { db } from "@/lib/firebase";
import { collection, addDoc, serverTimestamp } from "firebase/firestore";
import { useToast } from "@/hooks/use-toast";
import { useAuth } from "@/contexts/AuthContext";
import { FileEdit } from "lucide-react";

export const ContentEditor = () => {
  const [title, setTitle] = useState("");
  const [excerpt, setExcerpt] = useState("");
  const [content, setContent] = useState("");
  const [category, setCategory] = useState("");
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();
  const { user } = useAuth();

  const handlePublish = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim()) {
      toast({
        title: "Erro",
        description: "Título e conteúdo são obrigatórios",
        variant: "destructive",
      });
      return;
    }

    setLoading(true);
    try {
      await addDoc(collection(db, "posts"), {
        title,
        excerpt: excerpt || title.substring(0, 160),
        content,
        category: category || "Geral",
        authorId: user?.uid,
        authorName: user?.email,
        published: true,
        thumbnail: "",
        createdAt: serverTimestamp(),
      });

      toast({
        title: "Sucesso",
        description: "Post publicado com sucesso",
      });

      setTitle("");
      setExcerpt("");
      setContent("");
      setCategory("");
    } catch (error) {
      console.error("Erro ao publicar post:", error);
      toast({
        title: "Erro",
        description: "Não foi possível publicar o post",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="col-span-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <FileEdit className="h-5 w-5" />
          Editor de Conteúdo
        </CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handlePublish} className="space-y-4">
          <div>
            <Label htmlFor="title">Título</Label>
            <Input
              id="title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Título do post"
              required
            />
          </div>

          <div>
            <Label htmlFor="excerpt">Resumo</Label>
            <Input
              id="excerpt"
              value={excerpt}
              onChange={(e) => setExcerpt(e.target.value)}
              placeholder="Breve resumo do conteúdo"
            />
          </div>

          <div>
            <Label htmlFor="category">Categoria</Label>
            <Input
              id="category"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              placeholder="Categoria do post"
            />
          </div>

          <div>
            <Label htmlFor="content">Conteúdo</Label>
            <RichTextEditor
              value={content}
              onChange={setContent}
              placeholder="Escreva seu conteúdo aqui..."
            />
          </div>

          <Button type="submit" disabled={loading} className="w-full">
            {loading ? "Publicando..." : "Publicar Post"}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
};
