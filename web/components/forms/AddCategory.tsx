"use client";

import { useEffect } from "react";
import { Field, FieldGroup, FieldLabel } from "../ui/field";
import { Input } from "../ui/input";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { Item, ItemContent } from "../ui/item";
import { Button } from "../ui/button";
import { Controller, useForm } from "react-hook-form";
import { useStore } from "@/lib/store/store";
import ErrorLabel from "../ErrorLabel/ErrorLabel";

const NullMeasureUnit = null;

interface CreateCategoryFormData {
  name: string;
  shortName: string;
  measureUnitId: number | null;
  baseClassId: string | number;
}

export default function AddCategory() {
  const {
    measures: { items, isLoading: isLoadingMeasures, fetchMeasures },
    categories: {
      isLoading: isLoadingCategory,
      createCategory,
      refreshList,
      error,
      clearError,
    },
  } = useStore();
  const {
    register,
    handleSubmit,
    control,
    formState: { isValid, errors },
  } = useForm({
    defaultValues: {
      name: "",
      shortName: "",
      measureUnitId: NullMeasureUnit,
      baseClassId: "",
    },
  });

  useEffect(() => {
    fetchMeasures();
  }, [fetchMeasures]);

  const handleCreate = async (data: CreateCategoryFormData) => {
    await createCategory({
      name: data.name,
      shortName: data.shortName,
      measureUnitId: data.measureUnitId,
      baseClassId: data.baseClassId === "" ? null : Number(data.baseClassId),
    });
    refreshList();
  };

  return (
    <form
      className="min-w-xs flex flex-col gap-2 bg-dimmedblue p-2 rounded-md text-background"
      onSubmit={handleSubmit(handleCreate)}
    >
      <FieldGroup>
        <Field>
          <FieldLabel htmlFor="category-name">Имя категории</FieldLabel>
          <Input
            id="category-name"
            placeholder="Имя"
            required
            {...register("name", { required: true })}
            className="bg-background text-foreground placeholder:text-gray-400"
          ></Input>
          {errors.name && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
        <Field>
          <FieldLabel>Краткое имя</FieldLabel>
          <Input
            id="category-shortName"
            placeholder="Короткое имя"
            {...register("shortName", { required: true })}
            required
            className="bg-background text-foreground placeholder:text-gray-400"
          ></Input>
          {errors.shortName && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
        {isLoadingMeasures ? (
          <Item>
            <ItemContent>Единицы измерения загружаются...</ItemContent>
          </Item>
        ) : (
          <Field>
            <FieldLabel>Единица измерения</FieldLabel>
            <Controller
              name="measureUnitId"
              control={control}
              defaultValue={NullMeasureUnit}
              render={({ field }) => (
                <Select onValueChange={field.onChange} value={field.value}>
                  <SelectTrigger className="bg-background text-foreground ">
                    <SelectValue placeholder="Единица измерения"></SelectValue>
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      <SelectLabel>Единицы измерения категории</SelectLabel>
                      <SelectItem value={NullMeasureUnit}>Без е. и.</SelectItem>
                      {items.map((m) => (
                        <SelectItem key={m.id} value={m.id}>
                          {`${m.name} (${m.shortName})`}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>
              )}
            ></Controller>
          </Field>
        )}
        <Field>
          <FieldLabel htmlFor="category-baseClass-id">
            Идентификатор родительской категории
          </FieldLabel>
          <Input
            id="category-baseClass-id"
            type="number"
            min={1}
            placeholder="Нет базовой категории"
            className="bg-background text-foreground placeholder:text-gray-400"
            {...register("baseClassId", {
              valueAsNumber: true,
              setValueAs: (value) => {
                if (value === "" || value === null || value === undefined)
                  return "";
                const num = Number(value);
                return isNaN(num) ? "" : num;
              },
            })}
          ></Input>
        </Field>
      </FieldGroup>
      <Button
        className="w-full disabled:opacity-40 hover:bg-accent"
        variant="secondary"
        type="submit"
        disabled={isLoadingCategory || !isValid}
      >
        Создать
      </Button>
      {error && (
        <ErrorLabel message={error.message} onClearError={() => clearError()} />
      )}
    </form>
  );
}
