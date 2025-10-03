import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";

export default function Privacy() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <div className="pt-24 pb-20">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <h1 className="text-4xl md:text-5xl font-bold mb-8">Política de Privacidade</h1>
          
          <div className="prose prose-lg max-w-none space-y-6 text-muted-foreground">
            <p className="text-sm italic">Última atualização: {new Date().toLocaleDateString('pt-PT')}</p>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">1. Introdução</h2>
              <p>
                A Carsai Mozambique LMS ("nós", "nosso" ou "Carsai") está comprometida em proteger sua privacidade. Esta Política de Privacidade explica como coletamos, usamos, divulgamos e protegemos suas informações quando você usa nossa plataforma de aprendizagem online.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">2. Informações que Coletamos</h2>
              <p>Coletamos vários tipos de informações:</p>
              
              <h3 className="text-xl font-semibold text-foreground mt-6 mb-3">2.1 Informações Fornecidas por Você</h3>
              <ul className="list-disc pl-6 space-y-2">
                <li>Nome completo e informações de contacto (email, telefone)</li>
                <li>Credenciais de login (email e senha criptografada)</li>
                <li>Informações de pagamento (processadas por terceiros seguros)</li>
                <li>Conteúdo que você cria (comentários, avaliações, mensagens)</li>
                <li>Informações do perfil (avatar, biografia, preferências)</li>
              </ul>

              <h3 className="text-xl font-semibold text-foreground mt-6 mb-3">2.2 Informações Coletadas Automaticamente</h3>
              <ul className="list-disc pl-6 space-y-2">
                <li>Dados de uso (páginas visitadas, tempo na plataforma, cursos acessados)</li>
                <li>Informações do dispositivo (tipo de dispositivo, sistema operacional, navegador)</li>
                <li>Endereço IP e dados de localização</li>
                <li>Cookies e tecnologias similares</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">3. Como Usamos Suas Informações</h2>
              <p>Utilizamos suas informações para:</p>
              <ul className="list-disc pl-6 space-y-2">
                <li>Fornecer, operar e manter nossa plataforma</li>
                <li>Processar transações e enviar confirmações</li>
                <li>Personalizar sua experiência de aprendizagem</li>
                <li>Enviar atualizações sobre cursos, novidades e promoções</li>
                <li>Responder a perguntas e fornecer suporte ao cliente</li>
                <li>Melhorar nossos serviços através de análises e pesquisas</li>
                <li>Detectar e prevenir fraudes e abusos</li>
                <li>Cumprir obrigações legais</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">4. Compartilhamento de Informações</h2>
              <p>Não vendemos suas informações pessoais. Podemos compartilhar suas informações com:</p>
              
              <h3 className="text-xl font-semibold text-foreground mt-6 mb-3">4.1 Fornecedores de Serviços</h3>
              <p>
                Compartilhamos informações com prestadores de serviços terceirizados que nos ajudam a operar a plataforma:
              </p>
              <ul className="list-disc pl-6 space-y-2">
                <li>Processadores de pagamento (M-Pesa, e-Mola, MKesh, PayPal)</li>
                <li>Serviços de hospedagem (Firebase/Google Cloud)</li>
                <li>Ferramentas de análise e marketing</li>
              </ul>

              <h3 className="text-xl font-semibold text-foreground mt-6 mb-3">4.2 Instrutores</h3>
              <p>
                Quando você se inscreve em um curso, compartilhamos informações básicas (nome, email, progresso) com o instrutor do curso.
              </p>

              <h3 className="text-xl font-semibold text-foreground mt-6 mb-3">4.3 Requisitos Legais</h3>
              <p>
                Podemos divulgar suas informações se exigido por lei ou em resposta a processos legais válidos.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">5. Segurança dos Dados</h2>
              <p>
                Implementamos medidas de segurança técnicas e organizacionais para proteger suas informações:
              </p>
              <ul className="list-disc pl-6 space-y-2">
                <li>Criptografia SSL/TLS para transmissão de dados</li>
                <li>Senhas criptografadas usando hash seguro</li>
                <li>Controles de acesso rigorosos aos dados</li>
                <li>Monitoramento regular de segurança</li>
                <li>Backups regulares dos dados</li>
              </ul>
              <p className="mt-4">
                No entanto, nenhum método de transmissão pela Internet é 100% seguro. Não podemos garantir a segurança absoluta de suas informações.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">6. Seus Direitos</h2>
              <p>Você tem os seguintes direitos em relação aos seus dados pessoais:</p>
              <ul className="list-disc pl-6 space-y-2">
                <li><strong>Acesso:</strong> Solicitar uma cópia de suas informações pessoais</li>
                <li><strong>Correção:</strong> Solicitar correção de informações imprecisas</li>
                <li><strong>Exclusão:</strong> Solicitar a exclusão de seus dados pessoais</li>
                <li><strong>Portabilidade:</strong> Receber seus dados em formato estruturado</li>
                <li><strong>Oposição:</strong> Opor-se ao processamento de seus dados</li>
                <li><strong>Retirada de Consentimento:</strong> Retirar o consentimento a qualquer momento</li>
              </ul>
              <p className="mt-4">
                Para exercer esses direitos, entre em contacto conosco através de suporte@carsai.co.mz
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">7. Cookies e Tecnologias Similares</h2>
              <p>
                Usamos cookies e tecnologias similares para melhorar sua experiência. Você pode controlar cookies através das configurações do seu navegador. Os cookies que usamos incluem:
              </p>
              <ul className="list-disc pl-6 space-y-2">
                <li><strong>Cookies Essenciais:</strong> Necessários para o funcionamento da plataforma</li>
                <li><strong>Cookies de Desempenho:</strong> Ajudam a entender como você usa a plataforma</li>
                <li><strong>Cookies de Funcionalidade:</strong> Lembram suas preferências</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">8. Retenção de Dados</h2>
              <p>
                Mantemos suas informações pessoais pelo tempo necessário para fornecer nossos serviços e cumprir nossas obrigações legais. Quando você solicita a exclusão de sua conta, excluímos ou anonimizamos seus dados pessoais, exceto quando precisamos retê-los para fins legais.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">9. Crianças e Menores</h2>
              <p>
                Nossa plataforma não é direcionada a menores de 13 anos. Se tomarmos conhecimento de que coletamos informações de uma criança menor de 13 anos sem o consentimento dos pais, tomaremos medidas para excluir essas informações.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">10. Transferências Internacionais de Dados</h2>
              <p>
                Seus dados podem ser transferidos e processados em países fora de Moçambique, incluindo servidores na Europa e Estados Unidos. Garantimos que essas transferências cumprem as leis aplicáveis de proteção de dados.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">11. Alterações a Esta Política</h2>
              <p>
                Podemos atualizar esta Política de Privacidade periodicamente. Notificaremos você sobre alterações significativas através da plataforma ou por email. O uso continuado após alterações constitui aceitação da nova política.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">12. Contacto</h2>
              <p>
                Para questões sobre esta Política de Privacidade ou sobre como tratamos seus dados pessoais:
              </p>
              <ul className="list-none space-y-1">
                <li><strong>Email:</strong> suporte@carsai.co.mz</li>
                <li><strong>Telefone:</strong> +258 844 414 345</li>
                <li><strong>WhatsApp:</strong> +258 862 414 345</li>
                <li><strong>Endereço:</strong> Maputo, Moçambique</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-semibold text-foreground mt-8 mb-4">13. Autoridade de Proteção de Dados</h2>
              <p>
                Se você acredita que seus direitos de proteção de dados foram violados, você tem o direito de apresentar uma reclamação junto à autoridade de proteção de dados competente em Moçambique.
              </p>
            </section>
          </div>
        </div>
      </div>
      
      <Footer />
    </div>
  );
}
