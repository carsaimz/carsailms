import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";

export default function Terms() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <div className="pt-24 pb-20">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <h1 className="text-4xl md:text-5xl font-bold mb-8">Termos de Uso</h1>
          
          <div className="prose prose-lg max-w-none space-y-6 text-muted-foreground">
            <p className="text-sm italic">Última atualização: {new Date().toLocaleDateString('pt-PT')}</p>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">1. Aceitação dos Termos</h2>
              <p>
                Ao acessar e usar a plataforma Carsai Mozambique LMS, você concorda em cumprir e estar vinculado aos seguintes termos e condições de uso. Se você não concordar com qualquer parte destes termos, não deverá usar nossos serviços.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">2. Uso da Plataforma</h2>
              <p>
                A Carsai LMS é uma plataforma de educação online que fornece cursos, materiais didáticos e certificados. Você concorda em:
              </p>
              <ul className="list-disc pl-6 space-y-2">
                <li>Usar a plataforma apenas para fins legais e educacionais</li>
                <li>Não compartilhar suas credenciais de acesso com terceiros</li>
                <li>Não copiar, distribuir ou modificar conteúdo protegido por direitos autorais sem autorização</li>
                <li>Fornecer informações precisas e atualizadas durante o registro</li>
                <li>Respeitar os direitos de propriedade intelectual de todos os materiais</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">3. Registro e Conta</h2>
              <p>
                Para acessar determinados recursos da plataforma, você precisará criar uma conta. Você é responsável por:
              </p>
              <ul className="list-disc pl-6 space-y-2">
                <li>Manter a confidencialidade de sua senha</li>
                <li>Todas as atividades que ocorram em sua conta</li>
                <li>Notificar-nos imediatamente sobre qualquer uso não autorizado</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">4. Pagamentos e Reembolsos</h2>
              <p>
                Os cursos pagos devem ser adquiridos através dos métodos de pagamento aceitos (M-Pesa, e-Mola, MKesh, PayPal). 
                Política de reembolso:
              </p>
              <ul className="list-disc pl-6 space-y-2">
                <li>Reembolsos podem ser solicitados dentro de 7 dias após a compra</li>
                <li>O curso não deve ter sido concluído em mais de 30%</li>
                <li>Reembolsos serão processados no mesmo método de pagamento original</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">5. Conteúdo do Usuário</h2>
              <p>
                Ao enviar comentários, avaliações ou outros conteúdos para a plataforma, você concede à Carsai LMS uma licença mundial, não exclusiva e isenta de royalties para usar, modificar e exibir esse conteúdo.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">6. Certificados</h2>
              <p>
                Os certificados emitidos pela Carsai LMS atestam a conclusão bem-sucedida de cursos na plataforma. Estes certificados:
              </p>
              <ul className="list-disc pl-6 space-y-2">
                <li>São válidos e verificáveis através do nosso sistema</li>
                <li>Podem ser compartilhados em redes profissionais</li>
                <li>Não substituem certificações oficiais ou diplomas acadêmicos formais</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">7. Propriedade Intelectual</h2>
              <p>
                Todo o conteúdo da plataforma, incluindo textos, gráficos, logos, vídeos e software, é propriedade da Carsai Mozambique ou de seus licenciadores e está protegido por leis de direitos autorais e outras leis de propriedade intelectual.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">8. Limitação de Responsabilidade</h2>
              <p>
                A Carsai LMS não será responsável por quaisquer danos diretos, indiretos, incidentais, consequenciais ou punitivos decorrentes do seu uso ou incapacidade de usar a plataforma.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">9. Modificações dos Termos</h2>
              <p>
                Reservamos o direito de modificar estes termos a qualquer momento. As alterações entrarão em vigor imediatamente após a publicação. O uso continuado da plataforma após alterações constitui aceitação dos novos termos.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">10. Lei Aplicável</h2>
              <p>
                Estes termos são regidos pelas leis da República de Moçambique. Quaisquer disputas serão resolvidas nos tribunais de Maputo, Moçambique.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">11. Contacto</h2>
              <p>
                Para questões sobre estes Termos de Uso, entre em contacto:
              </p>
              <ul className="list-none space-y-1">
                <li>Email: suporte@carsai.co.mz</li>
                <li>Telefone: +258 844 414 345</li>
                <li>WhatsApp: +258 862 414 345</li>
              </ul>
            </section>
          </div>
        </div>
      </div>
      
      <Footer />
    </div>
  );
}
