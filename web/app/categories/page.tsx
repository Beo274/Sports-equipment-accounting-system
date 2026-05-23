import { CategoriesList } from "@/components/CategoriesList/CategoriesList";
import AddCategory from "@/components/forms/AddCategory";
import SetListType from "@/components/forms/SetListType";

export default function CategoryPage() {
  return (
    <div className="h-full w-full flex flex-col gap-1 p-1">
      <h2 className="text-2xl bg-accent rounded-md p-1 font-bold">Категории</h2>
      <div className="h-full grid grid-cols-[1fr_max-content] gap-2 items-start justify-center">
        <section className="grid grid-cols-[1fr_max-content] gap-2">
          <div className="h-full p-1 pb-3 border-2 border-accent rounded-md">
            <CategoriesList />
          </div>
          <SetListType />
        </section>
        <section className="border-2 border-accent rounded-md p-2 min-w-60">
          <AddCategory />
        </section>
      </div>
    </div>
  );
}
