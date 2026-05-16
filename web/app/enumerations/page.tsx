import EnumerationsList from "@/components/EnumerationList/EnumerationList";
import AddEnumeration from "@/components/forms/AddEnumeration";

export default function EnumerationsPage() {
  return (
    <div className="h-full w-full flex flex-col gap-1 p-1">
      <h2 className="text-2xl bg-accent rounded-md p-1 font-bold">
        Перечисления
      </h2>
      <div className="h-full grid grid-cols-[1fr_max-content] gap-3">
        <section className="border-2 border-accent rounded-md pb-3">
          <EnumerationsList />
        </section>
        <section className="border-2 border-accent rounded-md min-w-60 p-1">
          <AddEnumeration />
        </section>
      </div>
    </div>
  );
}
