import EnumerationsList from "@/components/EnumerationList/EnumerationList";

export default function EnumerationsPage() {
  return (
    <div className="h-full w-full flex flex-col gap-1 p-1">
      <h2 className="text-2xl bg-accent rounded-md p-1 font-bold">
        Перечисления
      </h2>
      <div className="h-full">
        <section>
          <EnumerationsList />
        </section>
      </div>
    </div>
  );
}
