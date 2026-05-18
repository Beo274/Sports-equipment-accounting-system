import { useStore } from "@/lib/store/store";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldContent, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";

export default function SetListType() {
  const {
    categories: {
      listType,
      setListType,
      parentId,
      setParentId,
      childId,
      setChildId,
    },
  } = useStore();

  return (
    <RadioGroup
      className="col-start-2 flex flex-col justify-between gap-2 w-full"
      value={listType}
      onValueChange={setListType}
    >
      <div className="flex items-center justify-between rounded-lg border p-4 bg-dimmedblue text-background">
        <Field className="space-y-0.5">
          <FieldLabel
            htmlFor="all-categories"
            className="text-base flex flex-col items-start cursor-pointer"
          >
            <FieldTitle>Все категории</FieldTitle>
            <FieldContent className="text-xs text-background">
              Вывести все категории
            </FieldContent>
          </FieldLabel>
        </Field>
        <RadioGroupItem
          value="all"
          id="all-categories"
          className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
        />
      </div>

      <div className="flex items-center justify-between rounded-lg border p-4 bg-dimmedblue text-background">
        <Field className="space-y-0.5">
          <FieldLabel
            htmlFor="leaves-categories"
            className="text-base flex flex-col items-start cursor-pointer"
          >
            <FieldTitle>Конечные категории</FieldTitle>
            <FieldContent className="text-xs text-background">
              Вывести категории, не имеющие потомков
            </FieldContent>
          </FieldLabel>
        </Field>
        <RadioGroupItem
          value="leaves"
          id="leaves-categories"
          className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
        />
      </div>

      <div className="flex items-center justify-between rounded-lg gap-3 border p-4 bg-dimmedblue text-background">
        <Field className="space-y-0.5">
          <FieldLabel
            htmlFor="children-categories"
            className="text-base flex flex-col items-start cursor-pointer"
          >
            <FieldTitle>Категории-потомки</FieldTitle>
            <FieldContent className="text-xs text-background">
              Вывести категории-потомки заданного класса
            </FieldContent>
          </FieldLabel>
        </Field>
        <Input
          id="category-children-class-id"
          type="number"
          min={0}
          placeholder="ID класса"
          className="max-w-24 bg-background text-foreground placeholder:text-gray-400"
          value={parentId}
          onChange={(e) => setParentId(e.target.value)}
          disabled={listType !== "children"}
        />
        <RadioGroupItem
          value="children"
          id="children-categories"
          className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
        />
      </div>

      <div className="flex items-center justify-between rounded-lg gap-3 border p-4 bg-dimmedblue text-background">
        <Field className="space-y-0.5">
          <FieldLabel
            htmlFor="parents-categories"
            className="text-base flex flex-col items-start cursor-pointer"
          >
            <FieldTitle>Категории-предки</FieldTitle>
            <FieldContent className="text-xs text-background">
              Вывести категории-предки заданного класса
            </FieldContent>
          </FieldLabel>
        </Field>
        <Input
          id="category-parents-class-id"
          type="number"
          min={0}
          value={childId}
          onChange={(e) => setChildId(e.target.value)}
          placeholder="ID класса"
          className="max-w-24 bg-background text-foreground placeholder:text-gray-400"
          disabled={listType !== "parents"}
        />
        <RadioGroupItem
          value="parents"
          id="parents-categories"
          className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
        />
      </div>
    </RadioGroup>
  );
}
