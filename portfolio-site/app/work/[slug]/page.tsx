import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { CaseStudy } from "@/components/CaseStudy";
import { getProject } from "@/lib/content";

type Props = { params: { slug: string } };

const slugs = ["caseflow", "dispatchgrid", "relateai"] as const;

export function generateStaticParams() {
  return slugs.map((slug) => ({ slug }));
}

export function generateMetadata({ params }: Props): Metadata {
  const project = getProject(params.slug);
  if (!project) return { title: "Work" };
  return {
    title: project.name,
    description: project.tagline,
  };
}

export default function WorkPage({ params }: Props) {
  const project = getProject(params.slug);
  if (!project) notFound();
  return <CaseStudy project={project} />;
}
