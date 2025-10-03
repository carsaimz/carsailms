import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";
import { Card, CardContent } from "@/components/ui/card";
import { Target, Eye, Heart, Award } from "lucide-react";

export default function About() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <div className="pt-24 pb-20">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              Sobre a Carsai Mozambique
            </h1>
            <p className="text-xl text-muted-foreground">
              Transformando o futuro da educação em Moçambique
            </p>
          </div>

          <div className="prose prose-lg max-w-none mb-12">
            <p className="text-muted-foreground leading-relaxed">
              A Carsai Mozambique é uma plataforma de ensino online dedicada a fornecer
              educação de qualidade e acessível para estudantes em Moçambique e além.
              Nossa missão é democratizar o acesso ao conhecimento e capacitar indivíduos
              com as habilidades necessárias para prosperar na economia digital.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-12">
            <Card className="hover:shadow-lg transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-center gap-3 mb-4">
                  <div className="bg-gradient-to-r from-primary to-secondary p-3 rounded-lg">
                    <Target className="w-6 h-6 text-white" />
                  </div>
                  <h3 className="text-xl font-semibold">Nossa Missão</h3>
                </div>
                <p className="text-muted-foreground">
                  Proporcionar educação de qualidade, acessível e relevante que capacita
                  os moçambicanos a alcançarem seus objetivos profissionais e pessoais.
                </p>
              </CardContent>
            </Card>

            <Card className="hover:shadow-lg transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-center gap-3 mb-4">
                  <div className="bg-gradient-to-r from-primary to-secondary p-3 rounded-lg">
                    <Eye className="w-6 h-6 text-white" />
                  </div>
                  <h3 className="text-xl font-semibold">Nossa Visão</h3>
                </div>
                <p className="text-muted-foreground">
                  Ser a principal plataforma de educação online em Moçambique, reconhecida
                  pela excelência e impacto na transformação de vidas.
                </p>
              </CardContent>
            </Card>

            <Card className="hover:shadow-lg transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-center gap-3 mb-4">
                  <div className="bg-gradient-to-r from-primary to-secondary p-3 rounded-lg">
                    <Heart className="w-6 h-6 text-white" />
                  </div>
                  <h3 className="text-xl font-semibold">Nossos Valores</h3>
                </div>
                <p className="text-muted-foreground">
                  Excelência, inovação, inclusão e compromisso com o desenvolvimento
                  contínuo dos nossos estudantes e comunidade.
                </p>
              </CardContent>
            </Card>

            <Card className="hover:shadow-lg transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-center gap-3 mb-4">
                  <div className="bg-gradient-to-r from-primary to-secondary p-3 rounded-lg">
                    <Award className="w-6 h-6 text-white" />
                  </div>
                  <h3 className="text-xl font-semibold">Nosso Compromisso</h3>
                </div>
                <p className="text-muted-foreground">
                  Garantir que cada estudante tenha acesso a recursos de aprendizagem
                  de classe mundial e suporte personalizado em sua jornada educacional.
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
      
      <Footer />
    </div>
  );
}
