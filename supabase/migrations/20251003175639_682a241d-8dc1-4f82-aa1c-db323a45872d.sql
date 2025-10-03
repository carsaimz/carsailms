-- Fix security definer view by recreating with security_invoker=on
DROP VIEW IF EXISTS public.courses_with_stats;

CREATE VIEW public.courses_with_stats
WITH (security_invoker=on)
AS
SELECT 
  c.*,
  COUNT(DISTINCT e.id) as enrollment_count,
  COUNT(DISTINCT l.id) as lesson_count,
  COALESCE(AVG(e.progress_percentage), 0) as avg_progress
FROM public.courses c
LEFT JOIN public.enrollments e ON e.course_id = c.id
LEFT JOIN public.lessons l ON l.course_id = c.id
GROUP BY c.id;