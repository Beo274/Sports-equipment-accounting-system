import AddParameter from "@/components/forms/AddParameter";
import ParametersList from "@/components/ParametersList/ParametersList";

export default function ParametersPage() {
  return (
    <div className="h-full w-full flex flex-col gap-1 p-1">
      <h2 className="text-2xl bg-accent rounded-md p-1 font-bold text-warm-white">
        Параметры
      </h2>
      <div className="min-h-0 flex-1 grid grid-cols-[1fr_max-content] gap-3">
        <section className="border-2 border-accent rounded-md p-1 flex flex-col min-h-0">
          <ParametersList />
        </section>
        <section className="h-max border-2 border-accent rounded-md min-w-60 p-1">
          <AddParameter />
        </section>
      </div>
    </div>
  );
}
