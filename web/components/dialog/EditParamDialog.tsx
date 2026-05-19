import {
  Dispatch,
  SetStateAction,
  useCallback,
  useEffect,
  useMemo,
} from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
} from "../ui/dialog";
import Parameter from "@/types/parameter";
import { useStore } from "@/lib/store/store";
import { Field, FieldGroup, FieldLabel } from "../ui/field";
import { Input } from "../ui/input";
import { NullMeasureUnit } from "@/types/measureUnit";
import { Controller, useForm } from "react-hook-form";
import { Button } from "../ui/button";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import UpdateParamDto from "@/lib/dto/updateParamDto";

interface EditParamDialogProps {
  isOpen: boolean;
  onOpenChange: Dispatch<SetStateAction<boolean>>;
  editedParam: Parameter | null;
}

interface UpdateFormSchema {
  name: string;
  shortName: string;
  measureId: string | typeof NullMeasureUnit;
}

export default function EditParamDialog({
  isOpen,
  onOpenChange,
  editedParam,
}: EditParamDialogProps) {
  const {
    measures: { items, isLoading, fetchMeasures },
    parameters: {
      updateParameter,
      isLoading: isLoadingParams,
      fetchParameters,
    },
  } = useStore();
  const {
    register,
    formState: { errors, isValid },
    reset,
    control,
    handleSubmit,
    watch,
  } = useForm<UpdateFormSchema>({
    defaultValues: {
      name: "",
      shortName: "",
      measureId: NullMeasureUnit,
    },
  });

  useEffect(() => {
    if (!isLoading && !items.length) {
      fetchMeasures();
    }
  }, []);

  useEffect(() => {
    if (isOpen && editedParam) {
      reset({
        name: editedParam.name ?? "",
        shortName: editedParam.shortName ?? "",
        measureId: String(editedParam.measureUnitId ?? NullMeasureUnit),
      });
    } else if (!isOpen) {
      reset({
        name: "",
        shortName: "",
        measureId: NullMeasureUnit,
      });
    }
  }, [isOpen, editedParam, reset]);

  const paramMeasure = useMemo(() => {
    if (editedParam) {
      return (
        items.find((m) => m.id === editedParam.measureUnitId) ?? NullMeasureUnit
      );
    }
  }, [editedParam, items]);

  const formMeasure = watch("measureId");

  const onSubmit = useCallback(
    async (data: UpdateFormSchema) => {
      if (editedParam) {
        const dto: UpdateParamDto = {
          name: data.name,
          shortName: data.shortName,
          measureId: null,
        };

        if (
          data.measureId !== NullMeasureUnit &&
          !isNaN(Number(data.measureId))
        ) {
          dto.measureId = Number(data.measureId);
        }

        if (
          dto.name === editedParam.name &&
          dto.shortName === editedParam.shortName &&
          dto.measureId === editedParam.measureUnitId
        ) {
          return;
        }

        await updateParameter(editedParam.id, dto);
        onOpenChange(false);
        fetchParameters();
      }
    },
    [editedParam, onOpenChange, updateParameter, fetchParameters],
  );

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogTitle>Изменение параметра</DialogTitle>
        {editedParam && (
          <DialogDescription>
            Параметр:{" "}
            <span className="bg-accent px-2 py-1 font-bold rounded-md">{`${editedParam.id}: ${editedParam.name} (${editedParam.shortName})`}</span>
            <br />
            {paramMeasure !== NullMeasureUnit &&
              paramMeasure &&
              `Единица измерения: ${paramMeasure.name} (${paramMeasure.shortName})`}
          </DialogDescription>
        )}
        <form
          className="flex flex-col items-center gap-1"
          onSubmit={handleSubmit(onSubmit)}
        >
          <FieldGroup>
            <Field>
              <FieldLabel>Название</FieldLabel>
              <Input
                type="text"
                id="update-param-name"
                {...register("name", { required: true })}
              />
              {errors.name && (
                <span className="text-accent text-xs">Обязательное поле</span>
              )}
            </Field>
            <Field>
              <FieldLabel>Короткое название</FieldLabel>
              <Input
                type="text"
                id="update-param-shortName"
                {...register("shortName", { required: true })}
              />
              {errors.shortName && (
                <span className="text-accent text-xs">Обязательное поле</span>
              )}
            </Field>
            <Field>
              <Controller
                name="measureId"
                control={control}
                defaultValue={NullMeasureUnit}
                render={({ field }) => (
                  <Select value={field.value} onValueChange={field.onChange}>
                    <SelectTrigger className="bg-background cursor-pointer">
                      <SelectValue
                        placeholder="Единица измерения"
                        className="text-gray-400"
                      >
                        {formMeasure === NullMeasureUnit ? (
                          <span>{NullMeasureUnit}</span>
                        ) : (
                          (() => {
                            const selected = items.find(
                              (m) => String(m.id) === formMeasure,
                            );
                            return selected ? (
                              <span>{`${selected.name} (${selected.shortName})`}</span>
                            ) : null;
                          })()
                        )}
                      </SelectValue>
                    </SelectTrigger>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Единицы измерения</SelectLabel>
                        <SelectItem value={NullMeasureUnit}>
                          Без е. и.
                        </SelectItem>
                        {items.map((m) => (
                          <SelectItem key={m.id} value={String(m.id)}>
                            {`${m.name} (${m.shortName})`}
                          </SelectItem>
                        ))}
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                )}
              />
            </Field>
          </FieldGroup>
          <Button
            className="hover:bg-accent min-w-30"
            variant="secondary"
            disabled={!isValid || isLoadingParams}
            type="submit"
          >
            Обновить
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}
