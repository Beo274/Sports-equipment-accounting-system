import Product from "@/types/product";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import { Category } from "@/types/category";
import { Field, FieldGroup, FieldLabel, FieldSet } from "../ui/field";
import { useStore } from "@/lib/store/store";
import { NullParameterId } from "@/types/parameter";
import { EnumerationValue, NullEnumerationId } from "@/types/enumeration";
import { Controller, useForm } from "react-hook-form";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Button } from "../ui/button";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Label } from "../ui/label";
import { Input } from "../ui/input";
import {
  CreateClassParameterDto,
  CreateProductParameterDto,
} from "@/lib/dto/createEntityParameterDto";
import ErrorLabel from "../ErrorLabel/ErrorLabel";

export type DialogEntity =
  | {
      paramFor: "class";
      entity: Category | null;
    }
  | {
      paramFor: "product";
      entity: Product | null;
    };

interface AddParameterDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  editingEntity: DialogEntity;
}

interface AddParameterFormSchema {
  paramId: string | typeof NullParameterId;
  enumId: string | typeof NullEnumerationId;
  enumValueId: string | null;
  minVal: string;
  maxVal: string;
  intVal: string;
}

export default function AddParameterDialog({
  open,
  onOpenChange,
  editingEntity,
}: AddParameterDialogProps) {
  const {
    parameters: {
      fetchParameters,
      isLoading: isLoadingParams,
      error: paramError,
      clearError: clearParamError,
      items: params,
      addParameterToClass,
      addParameterToProduct,
    },
    enumerations: {
      fetchEnumerations,
      fetchEnumerationValues,
      errors: enumsErrors,
      isLoadingEnums,
      isLoadingValues,
      clearError: clearEnumError,
      enums,
      enumValues,
    },
  } = useStore();
  const {
    control,
    handleSubmit,
    formState: { isValid },
    register,
    watch,
    reset,
  } = useForm<AddParameterFormSchema>();
  const [valueType, setValueType] = useState<"enum" | "int">("enum");
  const [fetchedEnumValues, setFetchedEnumValues] =
    useState<ReadonlyArray<EnumerationValue> | null>(null);

  useEffect(() => {
    if (open) reset();
  }, [open]);

  useEffect(() => {
    if (!isLoadingParams && !params.length) {
      fetchParameters();
    }
  }, [isLoadingParams]);

  useEffect(() => {
    if (!isLoadingEnums && !enums.length) {
      fetchEnumerations();
    }
  }, [isLoadingEnums]);

  const enumId = watch("enumId");

  useEffect(() => {
    if (enumId && enumId !== NullEnumerationId) {
      const enumToValue = enums.find((e) => e.id === Number(enumId));
      fetchEnumerationValues([enumToValue!]);
      console.log("Выбрано перечисление:", enumId);
    }
  }, [enumId, fetchEnumerationValues]);

  useEffect(() => {
    if (enumId && enumValues.has(Number(enumId))) {
      setFetchedEnumValues(enumValues.get(Number(enumId))!);
    }
  }, [enumValues, enumId]);

  const onSubmit = async (data: AddParameterFormSchema) => {
    if (!editingEntity.entity) return;

    if (editingEntity.paramFor === "class") {
      switch (valueType) {
        case "enum":
          const enumDto: CreateClassParameterDto = {
            classId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: Number(data.enumValueId),
            maxVal: null,
            minVal: null,
          };
          await addParameterToClass(enumDto);
          break;
        case "int":
          const intDto: CreateClassParameterDto = {
            classId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: null,
            maxVal: Number(data.maxVal),
            minVal: Number(data.minVal),
          };
          await addParameterToClass(intDto);
      }
    } else {
      switch (valueType) {
        case "enum":
          const enumDto: CreateProductParameterDto = {
            productId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: Number(data.enumValueId),
            maxVal: null,
            minVal: null,
          };
          await addParameterToProduct(enumDto);
          break;
        case "int":
          const intDto: CreateProductParameterDto = {
            productId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: null,
            maxVal: Number(data.maxVal),
            minVal: Number(data.minVal),
          };
          await addParameterToProduct(intDto);
      }
    }

    if (!paramError) onOpenChange(false);
  };

  const name = useMemo(() => {
    if (editingEntity.entity) {
      switch (editingEntity.paramFor) {
        case "product":
          const product = editingEntity.entity;
          return `${product.id}: ${product.name} (${product.shortName})}`;
        case "class":
          const category = editingEntity.entity;
          return `${category.id}: ${category.name} (${category.shortName})`;
      }
    } else {
      return "";
    }
  }, [editingEntity]);

  const renderError = useCallback(() => {
    if (paramError)
      return (
        <ErrorLabel
          message={paramError.message}
          onClearError={() => {
            clearParamError();
          }}
        />
      );
    else if (enumsErrors.fetchingError) {
      return (
        <ErrorLabel
          message={enumsErrors.fetchingError.message}
          onClearError={() => {
            clearEnumError("fetchingError");
          }}
        />
      );
    }
  }, [paramError, enumsErrors, clearParamError, clearEnumError]);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      {!editingEntity.entity ? (
        <DialogContent>
          Сущность для добавления параметра не определена
        </DialogContent>
      ) : (
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Добавление параметра</DialogTitle>
            <DialogDescription>{name}</DialogDescription>
          </DialogHeader>
          <form
            onSubmit={handleSubmit(onSubmit)}
            className="flex flex-col gap-2"
          >
            <FieldSet>
              <Field>
                <Controller
                  name="paramId"
                  control={control}
                  defaultValue={NullParameterId}
                  rules={{
                    validate: async (value) => value !== NullParameterId,
                  }}
                  render={({ field }) => (
                    <Select
                      value={field.value}
                      onValueChange={field.onChange}
                      disabled={isLoadingParams}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="ID параметра"></SelectValue>
                      </SelectTrigger>
                      <SelectContent>
                        <SelectGroup>
                          <SelectLabel>Параметры</SelectLabel>
                          <SelectItem value={NullParameterId}>
                            {NullParameterId}
                          </SelectItem>
                          {params.map((p) => (
                            <SelectItem
                              key={p.id}
                              value={String(p.id)}
                            >{`${p.name} (${p.shortName})`}</SelectItem>
                          ))}
                        </SelectGroup>
                      </SelectContent>
                    </Select>
                  )}
                />
              </Field>
              <RadioGroup
                value={valueType}
                onValueChange={setValueType}
                className="border p-2 rounded-md"
              >
                <div className="flex gap-2 group">
                  <RadioGroupItem
                    value="enum"
                    id="enum-type"
                    className="group-hover:scale-120 transition-transform"
                  />
                  <Label htmlFor="enum-type">Перечисление</Label>
                </div>
                <div className="flex gap-2 group">
                  <RadioGroupItem
                    value="int"
                    id="int-type"
                    className="group-hover:scale-120 transition-transform"
                  />
                  <Label htmlFor="int-type">Числовой</Label>
                </div>
              </RadioGroup>
              {valueType === "enum" ? (
                <Field>
                  <Controller
                    name="enumId"
                    control={control}
                    defaultValue={NullEnumerationId}
                    rules={{
                      validate: async (value) => value !== NullEnumerationId,
                    }}
                    render={({ field }) => (
                      <Select
                        value={field.value}
                        onValueChange={field.onChange}
                        disabled={isLoadingEnums}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="ID перечисления"></SelectValue>
                        </SelectTrigger>
                        <SelectContent>
                          <SelectGroup>
                            <SelectLabel>Перечисления</SelectLabel>
                            <SelectItem value={NullEnumerationId}>
                              {NullEnumerationId}
                            </SelectItem>
                            {enums.map((e) => (
                              <SelectItem
                                key={e.id}
                                value={String(e.id)}
                              >{`${e.name} (${e.shortName})`}</SelectItem>
                            ))}
                          </SelectGroup>
                        </SelectContent>
                      </Select>
                    )}
                  />
                  {enumId !== NullEnumerationId && (
                    <Controller
                      name="enumValueId"
                      control={control}
                      defaultValue={null}
                      rules={{
                        validate: async (value) => {
                          if (value === null) return "Выберите значение";
                          return true;
                        },
                      }}
                      render={({ field }) => (
                        <Select
                          value={field.value}
                          onValueChange={field.onChange}
                          disabled={isLoadingValues}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Значение перечисления"></SelectValue>
                          </SelectTrigger>
                          <SelectContent>
                            <SelectGroup>
                              <SelectLabel>Значения</SelectLabel>
                              {fetchedEnumValues &&
                                fetchedEnumValues.map((v) =>
                                  enumValueToSelectItem(v),
                                )}
                            </SelectGroup>
                          </SelectContent>
                        </Select>
                      )}
                    />
                  )}
                </Field>
              ) : (
                <FieldGroup>
                  <Field>
                    <FieldLabel htmlFor="param-min-val">
                      Мин. значение
                    </FieldLabel>
                    <Input
                      id="param-min-val"
                      type="number"
                      {...register("minVal")}
                    />
                  </Field>
                  <Field>
                    <FieldLabel htmlFor="param-min-val">
                      Макс. значение
                    </FieldLabel>
                    <Input
                      id="param-max-val"
                      type="number"
                      {...register("maxVal")}
                    />
                  </Field>
                </FieldGroup>
              )}
            </FieldSet>
            <Button
              type="submit"
              className="hover:bg-accent"
              variant="secondary"
              disabled={
                !isValid || isLoadingParams || isLoadingEnums || isLoadingValues
              }
            >
              Добавить
            </Button>
            {(paramError || enumsErrors.fetchingError) && renderError()}
          </form>
        </DialogContent>
      )}
    </Dialog>
  );
}

function enumValueToSelectItem(ev: EnumerationValue) {
  if (ev.intValue) {
    return (
      <SelectItem key={ev.id} value={String(ev.id)}>
        {ev.intValue} {ev.measure && ev.measure.shortName}
      </SelectItem>
    );
  } else if (ev.stringValue) {
    return (
      <SelectItem key={ev.id} value={String(ev.id)}>
        {ev.stringValue}
      </SelectItem>
    );
  } else if (ev.imageValue) {
    return (
      <SelectItem key={ev.id} value={String(ev.id)}>
        Картинка:{" "}
        <a href={ev.imageValue} className="hover:text-accent">
          ссылка
        </a>
      </SelectItem>
    );
  } else return <SelectItem key={ev.id}>Значение неопределено</SelectItem>;
}
