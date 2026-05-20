import AddProduct from "@/components/forms/AddProduct";
import ProductsList from "@/components/ProductsList/ProductsList";

export default function ProductPage() {
  return (
    <div className="h-full w-full flex flex-col gap-1 p-1">
      <h2 className="text-2xl bg-accent rounded-md p-1 font-bold">Изделия</h2>
      <div className="h-full grid grid-cols-[1fr_max-content] gap-3">
        <section className="border-2 border-accent rounded-md pb-3">
          <ProductsList />
        </section>
        <section className="h-min flex flex-col gap-2 border-2 border-accent rounded-md min-w-60 p-1">
          <AddProduct />
        </section>
      </div>
    </div>
  );
}
