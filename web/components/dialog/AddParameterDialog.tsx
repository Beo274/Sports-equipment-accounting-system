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
import {
  Enumeration,
  EnumerationValue,
  NullEnumerationId,
} from "@/types/enumeration";
import {
  Control,
  Controller,
  FieldErrors,
  useForm,
  UseFormRegister,
} from "react-hook-form";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import {
  ComponentType,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from "react";
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
    formState: { isValid, errors },
    register,
    watch,
    reset,
  } = useForm<AddParameterFormSchema>({ mode: "onChange" });
  const [valueType, setValueType] = useState<"enum" | "int" | "range">("enum");
  const [fetchedEnumValues, setFetchedEnumValues] =
    useState<ReadonlyArray<EnumerationValue> | null>(null);

  useEffect(() => {
    if (open) {
      reset();
      fetchParameters();
      fetchEnumerations();
    }
  }, [open]);

  const enumId = watch("enumId");
  const minVal = watch("minVal");
  const maxVal = watch("maxVal");

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
            intVal: null,
          };
          await addParameterToClass(enumDto);
          break;
        case "range":
          const rangeDto: CreateClassParameterDto = {
            classId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: null,
            maxVal: Number(data.maxVal),
            minVal: Number(data.minVal),
            intVal: null,
          };
          await addParameterToClass(rangeDto);
          break;
        case "int":
          const intDto: CreateClassParameterDto = {
            classId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: null,
            maxVal: null,
            minVal: null,
            intVal: Number(data.intVal),
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
            intVal: null,
          };
          await addParameterToProduct(enumDto);
          break;
        case "range":
          const rangeDto: CreateProductParameterDto = {
            productId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: null,
            maxVal: Number(data.maxVal),
            minVal: Number(data.minVal),
            intVal: null,
          };
          await addParameterToProduct(rangeDto);
          break;
        case "int":
          const intDto: CreateProductParameterDto = {
            productId: editingEntity.entity.id,
            paramId: Number(data.paramId),
            enumValueId: null,
            maxVal: null,
            minVal: null,
            intVal: Number(data.intVal),
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

  const renderValueFields = () => {
    switch (valueType) {
      case "enum":
        return (
          <EnumValueFields
            control={control}
            errors={errors}
            isLoadingEnums={isLoadingEnums}
            isLoadingValues={isLoadingValues}
            enumId={enumId}
            enums={enums}
            fetchedEnumValues={fetchedEnumValues}
          />
        );
      case "int":
        return (
          <IntValueFields
            register={register}
            errors={errors}
            valueType={valueType}
          />
        );
      case "range":
        return (
          <RangeValueFields
            register={register}
            errors={errors}
            maxVal={maxVal}
            minVal={minVal}
            valueType={valueType}
          />
        );
    }
  };

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
                    validate: async (value) => {
                      if (value === NullParameterId) return "Выберите параметр";
                      return true;
                    },
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
                            <SelectItem key={p.id} value={String(p.id)}>
                              {`${p.name} (${p.shortName})`}{" "}
                              {p.measure && p.measure.shortName}
                            </SelectItem>
                          ))}
                        </SelectGroup>
                      </SelectContent>
                    </Select>
                  )}
                />
                {errors.paramId && (
                  <span className="text-accent text-xs">
                    {errors.paramId.message}
                  </span>
                )}
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
                <div className="flex gap-2 group">
                  <RadioGroupItem
                    value="range"
                    id="range-type"
                    className="group-hover:scale-120 transition-transform"
                  />
                  <Label htmlFor="range-type">Диапазон</Label>
                </div>
              </RadioGroup>
              {renderValueFields()}
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

interface EnumValueFieldsProps {
  control: Control<AddParameterFormSchema>;
  errors: FieldErrors<AddParameterFormSchema>;
  isLoadingEnums: boolean;
  isLoadingValues: boolean;
  enums: ReadonlyArray<Enumeration>;
  enumId: string;
  fetchedEnumValues: ReadonlyArray<EnumerationValue> | null;
}

function EnumValueFields({
  control,
  errors,
  isLoadingEnums,
  isLoadingValues,
  enums,
  fetchedEnumValues,
  enumId,
}: EnumValueFieldsProps) {
  return (
    <Field>
      <Controller
        name="enumId"
        control={control}
        defaultValue={NullEnumerationId}
        rules={{
          validate: async (value) => {
            if (value === NullEnumerationId) return "Выберите перечисление";
            return true;
          },
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
      {errors.enumId && (
        <span className="text-accent text-xs">{errors.enumId.message}</span>
      )}
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
                    fetchedEnumValues.map((v) => enumValueToSelectItem(v))}
                </SelectGroup>
              </SelectContent>
            </Select>
          )}
        />
      )}
      {errors.enumValueId && (
        <span className="text-accent text-xs">
          {errors.enumValueId.message}
        </span>
      )}
    </Field>
  );
}

interface RangeValueFieldsProps {
  register: UseFormRegister<AddParameterFormSchema>;
  errors: FieldErrors<AddParameterFormSchema>;
  minVal: string;
  maxVal: string;
  valueType: string;
}

function RangeValueFields({
  register,
  errors,
  maxVal,
  minVal,
  valueType,
}: RangeValueFieldsProps) {
  return (
    <FieldGroup>
      <Field>
        <FieldLabel htmlFor="param-min-val">Мин. значение</FieldLabel>
        <Input
          id="param-min-val"
          type="number"
          {...register("minVal", {
            validate: {
              required: async (value) => {
                if (valueType === "int" && value === "")
                  return "Обязательное поле";
                return true;
              },
              lteMax: async (value) => {
                if (value && maxVal && Number(value) > Number(maxVal))
                  return "Минимум  не должен быть больше максимума";
                return true;
              },
            },
          })}
        />
        {errors.minVal && (
          <span className="text-accent text-xs">{errors.minVal.message}</span>
        )}
      </Field>
      <Field>
        <FieldLabel htmlFor="param-min-val">Макс. значение</FieldLabel>
        <Input
          id="param-max-val"
          type="number"
          {...register("maxVal", {
            validate: {
              required: async (value) => {
                if (valueType === "int" && value === "")
                  return "Обязательное поле";
                return true;
              },
              gteMin: async (value) => {
                if (value && minVal && Number(value) < Number(minVal))
                  return "Максимум не должен быть меньше минимума";
                return true;
              },
            },
          })}
        />
        {errors.maxVal && (
          <span className="text-accent text-xs">{errors.maxVal.message}</span>
        )}
      </Field>
    </FieldGroup>
  );
}

interface IntValueFieldsProps {
  register: UseFormRegister<AddParameterFormSchema>;
  errors: FieldErrors<AddParameterFormSchema>;
  valueType: string;
}

function IntValueFields({ register, errors, valueType }: IntValueFieldsProps) {
  return (
    <Field>
      <FieldLabel htmlFor="param-int-val">Числовое значение</FieldLabel>
      <Input
        id="param-int-val"
        type="number"
        {...register("intVal", {
          validate: async (value) => {
            if (valueType === "int" && isNaN(Number(value)))
              return "Обязательное поле";
            return true;
          },
        })}
      />
      {errors.intVal && (
        <span className="text-accent text-xs">{errors.intVal.message}</span>
      )}
    </Field>
  );
}
