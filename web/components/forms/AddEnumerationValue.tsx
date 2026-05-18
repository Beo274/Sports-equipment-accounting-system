"use client";

import { Controller, useForm } from "react-hook-form";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { useStore } from "@/lib/store/store";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { useEffect } from "react";
import { CreateEnumerationValueDto } from "@/lib/dto/createEnumerationValueDto";

const NullEnumerationId = "Не выбрано" as const;
const NullMeasureUnit = "Без е. и." as const;

const IntType = "Целое число" as const;
const StringType = "Строка" as const;
const ImageType = "Ссылка на картинку" as const;
type ValueType = typeof IntType | typeof StringType | typeof ImageType;
const ValueTypes = [IntType, StringType, ImageType];

interface FormSchema {
  enumerationId: typeof NullEnumerationId | string;
  valueType: ValueType;
  value: string;
  measureId: typeof NullMeasureUnit | string;
}

export default function AddEnumerationValue() {
  const {
    register,
    formState: { isValid, errors },
    reset,
    handleSubmit,
    control,
    watch,
  } = useForm<FormSchema>();
  const {
    enumerations: {
      enums,
      errors: { fetchingError, creatingEnumValueError },
      isLoadingValues,
      addEnumerationValue,
      fetchEnumerationValues,
    },
    measures: { items: measures, fetchMeasures },
  } = useStore();

  useEffect(() => {
    fetchMeasures();
  }, []);

  const valueType = watch("valueType");
  const measure = watch("measureId");

  const getInputType = (valueType: string) => {
    switch (valueType) {
      case IntType:
        return "number";
      case StringType:
        return "text";
      case ImageType:
        return "url";
      default:
        return "text";
    }
  };

  const onSubmit = async (data: FormSchema) => {
    const dto: CreateEnumerationValueDto = {
      enumerationId: Number(data.enumerationId),
      intValue: null,
      stringValue: null,
      imageValue: null,
      measureId: null,
    };

    switch (data.valueType) {
      case IntType:
        dto.intValue = Number(data.value);
        break;
      case StringType:
        dto.stringValue = data.value;
        break;
      case ImageType:
        dto.imageValue = data.value;
    }
    if (data.measureId !== NullMeasureUnit && !isNaN(Number(data.measureId))) {
      dto.measureId = Number(data.measureId);
    }
    await addEnumerationValue(dto);
    reset();
    await fetchEnumerationValues();
  };

  return (
    <form
      className="flex flex-col gap-2 bg-dimmedblue rounded-md text-background p-2"
      onSubmit={handleSubmit(onSubmit)}
    >
      <FieldTitle className="text-md font-bold">
        Создание значения пер-ния
      </FieldTitle>
      <FieldGroup>
        <Field>
          <FieldLabel>Перечисление</FieldLabel>
          <Controller
            name="enumerationId"
            control={control}
            defaultValue={NullEnumerationId}
            rules={{
              required: true,
              validate: async (v) => v !== NullEnumerationId,
            }}
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger className="bg-background cursor-pointer">
                  <SelectValue
                    placeholder="Перечисление"
                    className="bg-background placeholder:text-gray-400 text-foreground"
                  ></SelectValue>
                </SelectTrigger>
                <SelectContent>
                  {enums.map((e) => (
                    <SelectItem key={e.id} value={String(e.id)}>
                      {`${e.name} (${e.shortName})`}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          ></Controller>
          {errors.enumerationId && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
        <Field>
          <FieldTitle>Значение</FieldTitle>
          <Controller
            name="valueType"
            control={control}
            defaultValue={IntType}
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger className="bg-background cursor-pointer">
                  <SelectValue
                    placeholder="Тип значения"
                    className="bg-background placeholder:text-gray-400 text-foreground"
                  ></SelectValue>
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    {ValueTypes.map((t) => (
                      <SelectItem key={t} value={t}>
                        {t}
                      </SelectItem>
                    ))}
                  </SelectGroup>
                </SelectContent>
              </Select>
            )}
          />
          {errors.valueType && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
          <Input
            id="new-enum-value"
            type={getInputType(valueType)}
            className="bg-background placeholder:text-gray-400 text-foreground"
            placeholder="Значение"
            {...register("value", {
              required: true,
              validate: async (v) => {
                if (valueType === IntType) {
                  return !isNaN(Number(v));
                }
                return true;
              },
            })}
          />
          {errors.value && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
        <Field>
          <FieldLabel>Единица измерения</FieldLabel>
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
                    {measure === NullMeasureUnit ? (
                      <span>{NullMeasureUnit}</span>
                    ) : (
                      (() => {
                        const selected = measures.find(
                          (m) => String(m.id) === measure,
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
                    <SelectItem value={NullMeasureUnit}>Без е. и.</SelectItem>
                    {measures.map((m) => (
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
        variant="secondary"
        className="hover:bg-accent"
        disabled={!isValid || isLoadingValues}
        type="submit"
      >
        Создать
      </Button>
    </form>
  );
}
