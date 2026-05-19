"use client";

import { useStore } from "@/lib/store/store";
import { Button } from "../ui/button";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
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
import { useEffect } from "react";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import Loader from "../Loader/Loader";
import { Item } from "../ui/item";
import CreateParamDto from "@/lib/dto/createParamDto";
import { NullMeasureUnit } from "@/types/measureUnit";

interface FormSchema {
  name: string;
  shortName: string;
  measureId: typeof NullMeasureUnit | string;
}

export default function AddParameter() {
  const {
    measures: {
      items: measures,
      fetchMeasures,
      isLoading: isLoadingMeasures,
      error: errorMeasures,
      clearError: clearMeasuresError,
    },
    parameters: {
      isLoading: isLoadingParams,
      error: errorParams,
      clearError: clearParamError,
      createParameter,
      fetchParameters,
    },
  } = useStore();

  const {
    handleSubmit,
    formState: { errors, isValid },
    control,
    reset,
    register,
    watch,
  } = useForm<FormSchema>();

  const measure = watch("measureId");

  useEffect(() => {
    fetchMeasures();
  }, []);

  const onSubmit = async (data: FormSchema) => {
    const dto: CreateParamDto = {
      name: data.name,
      shortName: data.shortName,
      measureId: null,
    };

    if (data.measureId !== NullMeasureUnit && !isNaN(Number(data.measureId))) {
      dto.measureId = Number(data.measureId);
    }

    await createParameter(dto);
    reset();
    fetchParameters();
  };

  function renderMeasureSelect() {
    if (isLoadingMeasures) {
      return (
        <Item className="h-full justify-center items-center">
          <Loader />
        </Item>
      );
    }

    if (errorMeasures) {
      return (
        <ErrorLabel
          message={errorMeasures.message}
          onClearError={() => {
            clearMeasuresError();
            fetchMeasures();
          }}
        />
      );
    }

    return (
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
    );
  }

  return (
    <form
      className="flex flex-col gap-2 bg-dimmedblue rounded-md text-background p-2"
      onSubmit={handleSubmit(onSubmit)}
    >
      <FieldTitle className="text-md font-bold">Создание параметра</FieldTitle>
      <FieldGroup>
        <Field>
          <FieldLabel htmlFor="new-param-name">Название</FieldLabel>
          <Input
            id="new-param-name"
            type="text"
            placeholder="Название"
            className="bg-background text-foreground placeholder:text-gray-400"
            {...register("name", { required: true })}
          />
          {errors.name && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
        <Field>
          <FieldLabel htmlFor="new-param-shortName">
            Короткое название
          </FieldLabel>
          <Input
            id="new-param-shortName"
            type="text"
            placeholder="Короткое название"
            className="bg-background text-foreground placeholder:text-gray-400"
            {...register("shortName", { required: true })}
          />
          {errors.shortName && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
        <Field>
          <FieldLabel>Единица измерения</FieldLabel>
          {renderMeasureSelect()}
        </Field>
      </FieldGroup>
      <Button
        type="submit"
        variant="secondary"
        className="hover:bg-accent"
        disabled={!isValid || isLoadingMeasures}
      >
        Создать
      </Button>
    </form>
  );
}
