import { CategoriesList } from "@/components/CategoriesList/CategoriesList";
import AddCategory from "@/components/forms/AddCategory";

export default function CategoryPage() {
  return (
    <div className="w-full flex flex-col gap-3 p-3">
      <h2>Страница категорий</h2>
      <div className="grid grid-cols-[1fr_max-content] gap-2 items-start justify-center">
        <section className="p-2">
          <h3>Вывод категорий</h3>
          <CategoriesList />
        </section>
        <section className="p-2">
          <h3>Создать новую категорию</h3>
          <AddCategory />
        </section>
      </div>
    </div>
  );
}
