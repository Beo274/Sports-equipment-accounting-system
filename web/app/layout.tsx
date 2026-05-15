import type { Metadata } from "next";
import { Ubuntu_Mono, Ubuntu_Sans } from "next/font/google";
import "./globals.css";
import { cn } from "@/lib/utils";
import ClientLayout from "./clientLayout";

const ubuntuSans = Ubuntu_Sans({
  style: "normal",
  variable: "--font-ubuntu-sans",
  weight: ["400", "500", "700"],
});

const ubuntuMono = Ubuntu_Mono({
  variable: "--font-ubuntu-mono",
  weight: ["400", "700"],
  style: "normal",
});

export const metadata: Metadata = {
  title: "SportsEquipment App",
  description:
    "Приложение для работы со справочником изделий спортивного инвентаря",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="ru"
      className={cn(
        "h-full",
        "antialiased",
        ubuntuSans.variable,
        ubuntuMono.variable,
      )}
    >
      <body className="min-h-screen flex flex-col bg-background text-foreground font-sans">
        <header className="flex flex-col items-start p-2 bg-dimmedblue">
          <h1 className="text-3xl py-1 font-bold text-background">
            Спортивный инвентарь
          </h1>
          <p className="font-medium bg-accent rounded-md p-2">
            Крутой справочник товаров спортивного инвентаря
          </p>
        </header>
        <ClientLayout>{children}</ClientLayout>
        <footer className="flex items-center justify-end px-5 min-h-32 bg-dimmedblue">
          <p className="text-background">Произведено Ogurchiki team</p>
        </footer>
      </body>
    </html>
  );
}
