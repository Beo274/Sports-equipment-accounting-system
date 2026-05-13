import AddCategory from "@/components/forms/AddCategory";

export default function CategoryPage() {
  return (
    <div className="w-full flex flex-col gap-3 p-3">
      <h2>Страница категорий</h2>
      <div className="grid grid-cols-[1fr_max-content] gap-2 items-center justify-center">
        <section>
          <h3>Вывод категорий</h3>
        </section>
        <section>
          <h3>Создать новую категорию</h3>
          <AddCategory />
        </section>
      </div>
    </div>
  );
}
