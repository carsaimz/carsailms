import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";
import { Button } from "@/components/ui/button";
import { CourseCard } from "@/components/CourseCard";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Link } from "react-router-dom";
import { useCourses } from "@/hooks/useCourses";
import { 
  GraduationCap, 
  Users, 
  BookOpen, 
  Award,
  TrendingUp,
  Shield,
  Zap
} from "lucide-react";
import heroBg from "@/assets/hero-bg.jpg";

const stats = [
  { icon: Users, label: "Estudantes Ativos", value: "5,000+" },
  { icon: BookOpen, label: "Cursos Disponíveis", value: "150+" },
  { icon: GraduationCap, label: "Instrutores Expert", value: "50+" },
  { icon: Award, label: "Certificados Emitidos", value: "3,200+" },
];

const features = [
  {
    icon: TrendingUp,
    title: "Aprenda no Seu Ritmo",
    description: "Acesso 24/7 a todos os cursos e materiais de aprendizagem",
  },
  {
    icon: Shield,
    title: "Certificados Reconhecidos",
    description: "Certificados validados e aceites no mercado de trabalho",
  },
  {
    icon: Zap,
    title: "Conteúdo Atualizado",
    description: "Cursos sempre atualizados com as últimas tendências",
  },
];

export default function Home() {
  const { courses: featuredCourses, loading } = useCourses(true, 3);
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      {/* Hero Section */}
      <section className="relative pt-24 pb-20 overflow-hidden">
        <div 
          className="absolute inset-0 z-0"
          style={{
            backgroundImage: `linear-gradient(135deg, rgba(99, 102, 241, 0.95), rgba(139, 92, 246, 0.95)), url(${heroBg})`,
            backgroundSize: 'cover',
            backgroundPosition: 'center',
          }}
        />
        
        <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-4xl mx-auto">
            <h1 className="text-4xl md:text-6xl font-bold text-white mb-6 animate-fade-in">
              Transforme Seu Futuro com
              <span className="block bg-gradient-to-r from-accent to-yellow-300 bg-clip-text text-transparent mt-2">
                Educação de Qualidade
              </span>
            </h1>
            <p className="text-xl text-white/90 mb-8 max-w-2xl mx-auto">
              Aceda a cursos profissionais ministrados por especialistas e
              desenvolva as habilidades que o mercado procura
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link to="/courses">
                <Button variant="hero" size="lg" className="shadow-2xl">
                  Explorar Cursos
                </Button>
              </Link>
              <Link to="/register">
                <Button 
                  variant="outline" 
                  size="lg" 
                  className="bg-white/10 text-white border-white/20 hover:bg-white/20 backdrop-blur-sm"
                >
                  Começar Grátis
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-12 bg-card border-y border-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {stats.map((stat, index) => (
              <div key={index} className="text-center">
                <div className="inline-flex items-center justify-center w-12 h-12 rounded-lg bg-gradient-to-r from-primary to-secondary mb-3">
                  <stat.icon className="w-6 h-6 text-white" />
                </div>
                <div className="text-3xl font-bold text-foreground mb-1">
                  {stat.value}
                </div>
                <div className="text-muted-foreground text-sm">
                  {stat.label}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Featured Courses */}
      <section className="py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Cursos em Destaque
            </h2>
            <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
              Explore os nossos cursos mais populares e comece a aprender hoje
            </p>
          </div>
          
          {loading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
              {[1, 2, 3].map((i) => (
                <Card key={i}>
                  <Skeleton className="h-48 w-full" />
                  <div className="p-6 space-y-4">
                    <Skeleton className="h-4 w-20" />
                    <Skeleton className="h-6 w-full" />
                    <Skeleton className="h-20 w-full" />
                  </div>
                </Card>
              ))}
            </div>
          ) : featuredCourses.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
              {featuredCourses.map((course) => (
                <CourseCard key={course.id} course={course} />
              ))}
            </div>
          ) : (
            <div className="text-center py-12 mb-12">
              <p className="text-muted-foreground text-lg">
                Cursos em breve! Configure o Firebase para adicionar cursos.
              </p>
            </div>
          )}
          
          <div className="text-center">
            <Link to="/courses">
              <Button variant="gradient" size="lg">
                Ver Todos os Cursos
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-20 bg-muted/30">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Por Que Escolher a Carsai LMS?
            </h2>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {features.map((feature, index) => (
              <Card key={index} className="text-center hover:shadow-lg transition-shadow">
                <CardContent className="pt-6">
                  <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gradient-to-r from-primary to-secondary mb-4">
                    <feature.icon className="w-8 h-8 text-white" />
                  </div>
                  <h3 className="text-xl font-semibold mb-2">{feature.title}</h3>
                  <p className="text-muted-foreground">{feature.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-gradient-to-r from-primary to-secondary text-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-4">
            Pronto para Começar Sua Jornada?
          </h2>
          <p className="text-xl mb-8 text-white/90">
            Junte-se a milhares de estudantes que já transformaram suas carreiras
          </p>
          <Link to="/register">
            <Button 
              variant="outline" 
              size="lg" 
              className="bg-white text-primary hover:bg-white/90 border-0 shadow-xl"
            >
              Registar Agora
            </Button>
          </Link>
        </div>
      </section>

      <Footer />
    </div>
  );
}
