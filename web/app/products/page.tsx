import AddProduct from "@/components/forms/AddProduct";
import SetProductSort from "@/components/forms/SetProductSort";
import ProductsList from "@/components/ProductsList/ProductsList";

export default function ProductPage() {
  return (
    <div className="h-full w-full flex flex-col gap-1 p-1">
      <h2 className="text-2xl bg-accent rounded-md p-1 font-bold">Изделия</h2>
      <div className="h-full grid grid-cols-[1fr_max-content] gap-2">
        <section className="grid grid-cols-[1fr_max-content] gap-2">
          <div className="h-full p-1 pb-3 border-2 border-accent rounded-md">
            <ProductsList />
          </div>
          <div>
            <SetProductSort />
          </div>
        </section>
        <section className="h-min flex flex-col gap-2 p-2 border-2 border-accent rounded-md min-w-60">
          <AddProduct />
        </section>
      </div>
    </div>
  );
}
