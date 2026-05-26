import { Check } from "lucide-react";
import { Button } from "../ui/button";
import { Dialog, DialogContent, DialogTitle } from "../ui/dialog";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { useForm } from "react-hook-form";
import { useMemo } from "react";
import { useStore } from "@/lib/store/store";
import { Enumeration, EnumerationValue } from "@/types/enumeration";

interface ReorderEnumValuesDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;

  editingEnum: Enumeration | null;
}

export default function ReorderEnumValuesDialog({
  open,
  onOpenChange,
  editingEnum,
}: ReorderEnumValuesDialogProps) {
  const {
    enumerations: { enumValues, reorderValues, fetchEnumerationValues },
  } = useStore();
  const {
    formState: { errors, isValid },
    register,
    handleSubmit,
    reset,
  } = useForm<{ order: number[] }>({
    defaultValues: {
      order: [],
    },
  });

  const { values, valuesIds } = useMemo(() => {
    if (editingEnum) {
      const values = enumValues.get(editingEnum.id);
      if (!values)
        return {
          values: [] as ReadonlyArray<EnumerationValue>,
          valuesIds: new Set<number>(),
        };
      return {
        values,
        valuesIds: new Set(values.map((item) => item.id)),
      };
    } else {
      return {
        values: [] as ReadonlyArray<EnumerationValue>,
        valuesIds: new Set<number>(),
      };
    }
  }, [enumValues, editingEnum]);

  const onReorder = async (data: { order: number[] }) => {
    await reorderValues(data.order);
    reset();
    onOpenChange(false);
    await fetchEnumerationValues();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogTitle className="text-lg font-bold">
          Изменение порядка значений
        </DialogTitle>
        <form
          className="flex flex-col gap-2"
          onSubmit={handleSubmit(onReorder)}
        >
          <FieldGroup className="flex flex-row">
            <Field>
              <FieldTitle>
                Идентификаторы значений:{" "}
                <span className="bg-accent px-1 rounded-md font-bold text-warm-white">
                  {values.map((v) => v.id).join(" , ")}
                </span>
              </FieldTitle>
              <FieldLabel>
                Новый порядок (идентификаторы через пробел)
              </FieldLabel>
              <Input
                type="text"
                placeholder="Расставьте идентификаторы"
                {...register("order", {
                  required: true,
                  setValueAs: (value: unknown): number[] => {
                    if (typeof value !== "string" || !value.trim()) {
                      return [];
                    }

                    return value
                      .split(/\s+/)
                      .filter((item) => item !== "")
                      .map(Number);
                  },
                  validate: {
                    notEmpty: (value: number[]): string | boolean =>
                      value.length > 0 || "Введите числа",

                    correctLength: (value: number[]): string | boolean =>
                      value.length === values.length ||
                      `Должно быть ${values.length} чисел`,

                    allNumbers: (value: number[]): string | boolean =>
                      !value.some(isNaN) || "Все значения должны быть числами",

                    allExist: (value: number[]): string | boolean => {
                      const numbers = value.filter((n) => !isNaN(n));
                      if (numbers.length === 0) return true;

                      const missingIds = numbers.filter(
                        (id) => !valuesIds.has(id),
                      );

                      if (missingIds.length > 0) {
                        return `Не найдены ID: ${missingIds.join(", ")}`;
                      }
                      return true;
                    },

                    noDuplicates: (value: number[]): string | boolean => {
                      const numbers = value.filter((n) => !isNaN(n));
                      const uniqueIds = new Set(numbers);
                      if (uniqueIds.size !== numbers.length) {
                        return "Есть повторяющиеся ID";
                      }
                      return true;
                    },
                  },
                })}
              />
            </Field>
            <Button
              type="submit"
              variant="secondary"
              className="hover:bg-accent self-end"
              disabled={!isValid}
            >
              <Check />
            </Button>
          </FieldGroup>
          {errors.order && (
            <span className="text-accent text-xs">{errors.order.message}</span>
          )}
        </form>
      </DialogContent>
    </Dialog>
  );
}
