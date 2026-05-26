"use client";

import { useState } from "react";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
} from "../ui/sidebar";
import Link from "next/link";
import MeasuresDialog from "../dialog/MeasuresDialog";
import { Button } from "../ui/button";

interface NavSidebarProps {
  routes: RouteDescrioption[];
}

export interface RouteDescrioption {
  name: string;
  href: string;
}

export default function NavSidebar(props: NavSidebarProps) {
  const [isMeasuresOpen, setMeasuresOpen] = useState(false);

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
            <SidebarMenuItem>
              <Button
                variant="ghost"
                className="hover:bg-accent hover:text-background font-normal p-1"
                type="button"
                onClick={() => setMeasuresOpen(true)}
              >
                Единицы измерения
              </Button>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroup>
        <MeasuresDialog open={isMeasuresOpen} onOpenChange={setMeasuresOpen} />
      </SidebarContent>
    </Sidebar>
  );
}
