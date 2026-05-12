export default function MainPage() {
  return (
    <div className="w-full flex flex-col gap-2 p-2 border-x border-foreground">
      <h2 className="text-4xl p-2 font-bold">Главная страница</h2>
      <section className="flex flex-col gap-1 p-2">
        <h3 className="text-2xl font-bold">О приложении</h3>
        <p className="p-2">
          SportsEquipment App - приложение для ведения справочника категорий
          товаров, изделий и их характеристик.
        </p>
      </section>
      <section className="p-2">
        <h3 className="text-2xl font-bold">Возможности сервиса</h3>
        <ul className="flex flex-col gap-3 list-disc pl-4">
          <li>
            <p className="p-2">
              Классификатор категорий товаров спортивного инвентаря
            </p>
          </li>
          <li>
            <p className="p-2">Ведение справочника перечислений</p>
          </li>
          <li>
            <p className="p-2">
              Работа с возможными параметрами категорий или товаров
            </p>
          </li>
          <li>
            <p className="p-2">
              Ведение справочника изделий, редактирование их характеристик
            </p>
          </li>
        </ul>
      </section>
    </div>
  );
}
