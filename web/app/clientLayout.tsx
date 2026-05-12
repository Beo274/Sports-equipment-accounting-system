"use client";
import { RouteDescrioption } from "@/components/NavSidebar/NavSidebar";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import dynamic from "next/dynamic";
import { ReactNode } from "react";

const NavSidebar = dynamic(() => import("@/components/NavSidebar/NavSidebar"), {
  ssr: false,
});

const appRoutes: RouteDescrioption[] = [
  {
    name: "Главная",
    href: "/home",
  },
  {
    name: "Категории товаров",
    href: "/categories",
  },
  {
    name: "Перечисления",
    href: "/enumerations",
  },
  {
    name: "Параметры",
    href: "/parameters",
  },
  {
    name: "Изделия",
    href: "/products",
  },
];

export default function ClientLayout({ children }: { children: ReactNode }) {
  return (
    <SidebarProvider defaultOpen={false}>
      <NavSidebar routes={appRoutes} />
      <SidebarTrigger className="hover:bg-accent" />
      {children}
    </SidebarProvider>
  );
}
