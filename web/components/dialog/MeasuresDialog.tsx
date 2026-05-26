import { useStore } from "@/lib/store/store";
import { Dialog, DialogContent, DialogTitle } from "../ui/dialog";
import { ScrollArea } from "../ui/scroll-area";
import { Item, ItemActions, ItemGroup, ItemTitle } from "../ui/item";
import MeasureUnit from "@/types/measureUnit";
import Loader from "../Loader/Loader";
import { useEffect } from "react";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { useForm } from "react-hook-form";
import { Trash } from "lucide-react";

interface MeasuresDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

interface AddMeasureFromSchema {
  name: string;
  shortName: string;
}

export default function MeasuresDialog({
  open,
  onOpenChange,
}: MeasuresDialogProps) {
  const {
    measures: {
      isLoading,
      items,
      error,
      clearError,
      fetchMeasures,
      addMeasure,
      deleteMeasure,
    },
  } = useStore();
  const {
    register,
    formState: { isValid, errors },
    handleSubmit,
    reset,
  } = useForm<AddMeasureFromSchema>({ mode: "onChange" });

  useEffect(() => {
    if (open) {
      fetchMeasures();
    }
  }, [open, fetchMeasures]);

  const onSubmit = async (data: AddMeasureFromSchema) => {
    await addMeasure(data);
    reset();
    fetchMeasures();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogTitle className="text-lg">Единицы измерения</DialogTitle>
        <ScrollArea className="max-h-40 p-2 pr-3 border rounded-md">
          {error ? (
            <ErrorLabel
              message={error.message}
              onClearError={() => {
                clearError();
                fetchMeasures();
              }}
            />
          ) : isLoading ? (
            <div>
              <Loader />
            </div>
          ) : (
            <ItemGroup>
              {items.map((m) => (
                <MeasureItem
                  key={m.id}
                  measure={m}
                  onDelete={async () => {
                    await deleteMeasure(m.id);
                    fetchMeasures();
                  }}
                />
              ))}
            </ItemGroup>
          )}
        </ScrollArea>
        <form
          className="flex flex-col items-stretch gap-1"
          onSubmit={handleSubmit(onSubmit)}
        >
          <FieldTitle className="text-lg">
            Добавить единицу измерения
          </FieldTitle>
          <FieldGroup>
            <Field>
              <FieldLabel htmlFor="new-measure-name">Название</FieldLabel>
              <Input
                id="new-measure-name"
                type="text"
                placeholder="Название"
                {...register("name", { required: "Обязательное поле" })}
              />
              {errors.name && (
                <span className="text-xs text-red-500">
                  {errors.name.message}
                </span>
              )}
            </Field>
            <Field>
              <FieldLabel htmlFor="new-measure-shortName">
                Короткое название
              </FieldLabel>
              <Input
                id="new-measure-shortName"
                type="text"
                placeholder="Короткое название"
                {...register("shortName", { required: "Обязательное поле" })}
              />
              {errors.shortName && (
                <span className="text-xs text-red-500">
                  {errors.shortName.message}
                </span>
              )}
            </Field>
            <Button
              variant="secondary"
              className="hover:bg-accent cursor-pointer"
              type="submit"
              disabled={isLoading || !isValid}
            >
              Добавить
            </Button>
          </FieldGroup>
        </form>
      </DialogContent>
    </Dialog>
  );
}

interface MeasureItemProps {
  measure: MeasureUnit;

  onDelete: () => Promise<void>;
}

function MeasureItem({ measure, onDelete }: MeasureItemProps) {
  return (
    <Item className="border border-accent justify-between">
      <ItemTitle>{`${measure.name} (${measure.shortName})`}</ItemTitle>
      <ItemActions>
        <Button
          type="button"
          onClick={() => onDelete()}
          className="hover:bg-accent cursor-pointer"
          variant="ghost"
        >
          <Trash />
        </Button>
      </ItemActions>
    </Item>
  );
}
