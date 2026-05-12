"use client";

import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
} from "../ui/sidebar";
import Link from "next/link";

interface NavSidebarProps {
  routes: RouteDescrioption[];
}

export interface RouteDescrioption {
  name: string;
  href: string;
}

export default function NavSidebar(props: NavSidebarProps) {
  return (
    <Sidebar>
      <SidebarHeader className="text-center">
        <h2 className="font-mono font-bold text-2xl">Навигация</h2>
      </SidebarHeader>
      <SidebarContent className="border-y-2">
        <SidebarGroup>
          <SidebarMenu className="gap-3">
            {props.routes.map((r, i) => (
              <SidebarMenuItem key={`route_${i}`}>
                <Link
                  className="rounded-md p-1 hover:bg-accent hover:text-background transition-colors"
                  href={r.href}
                >
                  {r.name}
                </Link>
              </SidebarMenuItem>
            ))}
          </SidebarMenu>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}
