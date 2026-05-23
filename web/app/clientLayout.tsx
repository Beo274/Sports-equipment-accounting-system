"use client";
import { RouteDescrioption } from "@/components/NavSidebar/NavSidebar";
import { Button } from "@/components/ui/button";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { StoreProvider } from "@/lib/store/store";
import dynamic from "next/dynamic";
import { ReactNode, useState } from "react";

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
    <StoreProvider>
      <SidebarProvider defaultOpen={false} className="h-full min-h-full flex-1">
        <NavSidebar routes={appRoutes} />
        <SidebarTrigger className="hover:bg-accent" size="lg" />
        {children}
      </SidebarProvider>
    </StoreProvider>
  );
}
