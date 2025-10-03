-- Create enum for course levels
CREATE TYPE course_level AS ENUM ('beginner', 'intermediate', 'advanced');

-- Create enum for lesson content types
CREATE TYPE lesson_content_type AS ENUM ('video', 'pdf', 'text');

-- Create categories table
CREATE TABLE public.categories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL UNIQUE,
  slug TEXT NOT NULL UNIQUE,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Create courses table
CREATE TABLE public.courses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title TEXT NOT NULL,
  description TEXT,
  instructor_id UUID NOT NULL,
  category_id UUID REFERENCES public.categories(id) ON DELETE SET NULL,
  price DECIMAL(10,2) DEFAULT 0,
  level course_level DEFAULT 'beginner',
  thumbnail_url TEXT,
  published BOOLEAN DEFAULT false,
  featured BOOLEAN DEFAULT false,
  duration_hours INTEGER DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Create lessons table
CREATE TABLE public.lessons (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  course_id UUID NOT NULL REFERENCES public.courses(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  description TEXT,
  content_type lesson_content_type NOT NULL,
  content_url TEXT,
  text_content TEXT,
  order_index INTEGER NOT NULL,
  duration_minutes INTEGER DEFAULT 0,
  is_preview BOOLEAN DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(course_id, order_index)
);

-- Create enrollments table
CREATE TABLE public.enrollments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL,
  course_id UUID NOT NULL REFERENCES public.courses(id) ON DELETE CASCADE,
  enrolled_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  completed BOOLEAN DEFAULT false,
  completed_at TIMESTAMPTZ,
  progress_percentage INTEGER DEFAULT 0,
  UNIQUE(user_id, course_id)
);

-- Create lesson progress table
CREATE TABLE public.lesson_progress (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  enrollment_id UUID NOT NULL REFERENCES public.enrollments(id) ON DELETE CASCADE,
  lesson_id UUID NOT NULL REFERENCES public.lessons(id) ON DELETE CASCADE,
  completed BOOLEAN DEFAULT false,
  completed_at TIMESTAMPTZ,
  last_position INTEGER DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(enrollment_id, lesson_id)
);

-- Create indexes for better performance
CREATE INDEX idx_courses_instructor ON public.courses(instructor_id);
CREATE INDEX idx_courses_category ON public.courses(category_id);
CREATE INDEX idx_courses_published ON public.courses(published);
CREATE INDEX idx_lessons_course ON public.lessons(course_id);
CREATE INDEX idx_enrollments_user ON public.enrollments(user_id);
CREATE INDEX idx_enrollments_course ON public.enrollments(course_id);
CREATE INDEX idx_lesson_progress_enrollment ON public.lesson_progress(enrollment_id);

-- Enable RLS on all tables
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.courses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lessons ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.enrollments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lesson_progress ENABLE ROW LEVEL SECURITY;

-- RLS Policies for categories (public read, admin write)
CREATE POLICY "Categories are viewable by everyone" ON public.categories
  FOR SELECT USING (true);

CREATE POLICY "Only authenticated users can insert categories" ON public.categories
  FOR INSERT WITH CHECK (auth.uid() IS NOT NULL);

CREATE POLICY "Only authenticated users can update categories" ON public.categories
  FOR UPDATE USING (auth.uid() IS NOT NULL);

-- RLS Policies for courses
CREATE POLICY "Published courses are viewable by everyone" ON public.courses
  FOR SELECT USING (published = true OR auth.uid() = instructor_id);

CREATE POLICY "Instructors can insert their own courses" ON public.courses
  FOR INSERT WITH CHECK (auth.uid() = instructor_id);

CREATE POLICY "Instructors can update their own courses" ON public.courses
  FOR UPDATE USING (auth.uid() = instructor_id);

CREATE POLICY "Instructors can delete their own courses" ON public.courses
  FOR DELETE USING (auth.uid() = instructor_id);

-- RLS Policies for lessons
CREATE POLICY "Lessons of published courses are viewable" ON public.lessons
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM public.courses 
      WHERE courses.id = lessons.course_id 
      AND (courses.published = true OR courses.instructor_id = auth.uid())
    )
  );

CREATE POLICY "Instructors can manage lessons of their courses" ON public.lessons
  FOR ALL USING (
    EXISTS (
      SELECT 1 FROM public.courses 
      WHERE courses.id = lessons.course_id 
      AND courses.instructor_id = auth.uid()
    )
  );

-- RLS Policies for enrollments
CREATE POLICY "Users can view their own enrollments" ON public.enrollments
  FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can enroll in courses" ON public.enrollments
  FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own enrollments" ON public.enrollments
  FOR UPDATE USING (auth.uid() = user_id);

-- RLS Policies for lesson progress
CREATE POLICY "Users can view their own lesson progress" ON public.lesson_progress
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM public.enrollments 
      WHERE enrollments.id = lesson_progress.enrollment_id 
      AND enrollments.user_id = auth.uid()
    )
  );

CREATE POLICY "Users can update their own lesson progress" ON public.lesson_progress
  FOR ALL USING (
    EXISTS (
      SELECT 1 FROM public.enrollments 
      WHERE enrollments.id = lesson_progress.enrollment_id 
      AND enrollments.user_id = auth.uid()
    )
  );

-- Create storage buckets
INSERT INTO storage.buckets (id, name, public) 
VALUES 
  ('course-thumbnails', 'course-thumbnails', true),
  ('lesson-videos', 'lesson-videos', false),
  ('lesson-pdfs', 'lesson-pdfs', false);

-- Storage policies for course thumbnails (public read)
CREATE POLICY "Course thumbnails are publicly accessible" ON storage.objects
  FOR SELECT USING (bucket_id = 'course-thumbnails');

CREATE POLICY "Authenticated users can upload course thumbnails" ON storage.objects
  FOR INSERT WITH CHECK (bucket_id = 'course-thumbnails' AND auth.uid() IS NOT NULL);

CREATE POLICY "Users can update their own course thumbnails" ON storage.objects
  FOR UPDATE USING (bucket_id = 'course-thumbnails' AND auth.uid() IS NOT NULL);

-- Storage policies for lesson videos (enrolled users only)
CREATE POLICY "Enrolled users can view lesson videos" ON storage.objects
  FOR SELECT USING (
    bucket_id = 'lesson-videos' AND
    EXISTS (
      SELECT 1 FROM public.lessons
      JOIN public.enrollments ON enrollments.course_id = lessons.course_id
      WHERE lessons.content_url = storage.objects.name
      AND enrollments.user_id = auth.uid()
    )
  );

CREATE POLICY "Instructors can upload lesson videos" ON storage.objects
  FOR INSERT WITH CHECK (bucket_id = 'lesson-videos' AND auth.uid() IS NOT NULL);

-- Storage policies for lesson PDFs (enrolled users only)
CREATE POLICY "Enrolled users can view lesson PDFs" ON storage.objects
  FOR SELECT USING (
    bucket_id = 'lesson-pdfs' AND
    EXISTS (
      SELECT 1 FROM public.lessons
      JOIN public.enrollments ON enrollments.course_id = lessons.course_id
      WHERE lessons.content_url = storage.objects.name
      AND enrollments.user_id = auth.uid()
    )
  );

CREATE POLICY "Instructors can upload lesson PDFs" ON storage.objects
  FOR INSERT WITH CHECK (bucket_id = 'lesson-pdfs' AND auth.uid() IS NOT NULL);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updated_at
CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON public.categories
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_courses_updated_at BEFORE UPDATE ON public.courses
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_lessons_updated_at BEFORE UPDATE ON public.lessons
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default categories
INSERT INTO public.categories (name, slug, description) VALUES
  ('Programação', 'programacao', 'Cursos de desenvolvimento de software'),
  ('Design', 'design', 'Cursos de design gráfico e UI/UX'),
  ('Marketing', 'marketing', 'Cursos de marketing digital'),
  ('Negócios', 'negocios', 'Cursos de gestão e empreendedorismo'),
  ('Idiomas', 'idiomas', 'Cursos de línguas estrangeiras');

-- Create view for course with instructor info
CREATE OR REPLACE VIEW public.courses_with_stats AS
SELECT 
  c.*,
  COUNT(DISTINCT e.id) as enrollment_count,
  COUNT(DISTINCT l.id) as lesson_count,
  AVG(e.progress_percentage) as avg_progress
FROM public.courses c
LEFT JOIN public.enrollments e ON e.course_id = c.id
LEFT JOIN public.lessons l ON l.course_id = c.id
GROUP BY c.id;