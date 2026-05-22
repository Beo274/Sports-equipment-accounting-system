import { CategoriesList } from "@/components/CategoriesList/CategoriesList";
import AddCategory from "@/components/forms/AddCategory";

export default function CategoryPage() {
  return (
    <div className="h-full w-full flex flex-col gap-3 p-1">
      <h2 className="text-2xl bg-accent rounded-md p-1 font-bold">Категории</h2>
      <div className="h-full grid grid-cols-[1fr_max-content] gap-2 items-start justify-center">
        <section className="border-2 border-accent rounded-md p-3">
          <h3 className="p-1 text-lg">Вывод категорий</h3>
          <CategoriesList />
        </section>
        <section className="border-2 border-accent rounded-md p-2">
          <h3>Создать новую категорию</h3>
          <AddCategory />
        </section>
      </div>
    </div>
  );
}
