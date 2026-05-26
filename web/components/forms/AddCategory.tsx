"use client";

import { useEffect } from "react";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
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
import { Item } from "../ui/item";
import { Button } from "../ui/button";
import { Controller, useForm } from "react-hook-form";
import { useStore } from "@/lib/store/store";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import CreateCategoryDto from "@/lib/dto/createCategoryDto";
import { NullMeasureUnit } from "@/types/measureUnit";
import Loader from "../Loader/Loader";
import { Popover, PopoverContent, PopoverTrigger } from "../ui/popover";
import { Check, ChevronsUpDown } from "lucide-react";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
} from "../ui/command";
import { cn } from "@/lib/utils";

interface CreateCategoryFormData {
  name: string;
  shortName: string;
  measureUnitId: string | typeof NullMeasureUnit;
  baseClassId: string | number;
}

export default function AddCategory() {
  const {
    measures: {
      items: measures,
      isLoading: isLoadingMeasures,
      fetchMeasures,
      error: measuresError,
      clearError: clearMeasuresError,
    },
    categories: {
      items: categories,
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
    watch,
    reset,
  } = useForm<CreateCategoryFormData>({
    defaultValues: {
      name: "",
      shortName: "",
      measureUnitId: NullMeasureUnit,
      baseClassId: "",
    },
  });

  const measure = watch("measureUnitId");

  useEffect(() => {
    fetchMeasures();
    refreshList();
  }, [fetchMeasures, refreshList]);

  const handleCreate = async (data: CreateCategoryFormData) => {
    const dto: CreateCategoryDto = {
      name: data.name,
      shortName: data.shortName,
      measureUnitId: null,
      baseClassId: data.baseClassId === "" ? null : Number(data.baseClassId),
    };
    if (
      data.measureUnitId !== NullMeasureUnit &&
      !isNaN(Number(data.measureUnitId))
    ) {
      dto.measureUnitId = Number(data.measureUnitId);
    }
    await createCategory(dto);
    reset();
    refreshList();
  };

  return (
    <form
      className="flex flex-col gap-2 bg-dimmedblue p-2 rounded-md text-background"
      onSubmit={handleSubmit(handleCreate)}
    >
      <FieldTitle className="text-md font-bold">Добавить категорию</FieldTitle>
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
        {measuresError ? (
          <ErrorLabel
            message={measuresError.message}
            onClearError={() => {
              clearMeasuresError();
              fetchMeasures();
            }}
          />
        ) : isLoadingMeasures ? (
          <Item>
            <Loader />
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
            ></Controller>
          </Field>
        )}
        {isLoadingCategory ? (
          <Item>
            <Loader />
          </Item>
        ) : (
          <Field>
            <FieldLabel>Родительская категория</FieldLabel>
            <Controller
              name="baseClassId"
              control={control}
              render={({ field }) => (
                <Popover>
                  <PopoverTrigger>
                    <div
                      className={cn(
                        "inline-flex items-center justify-between w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background cursor-pointer",
                        !field.value ? "text-placeholder" : "text-foreground",
                      )}
                    >
                      {field.value
                        ? categories.find(
                            (cat) => cat.id === Number(field.value),
                          )?.name || "Выбрать категорию"
                        : "Без категории"}
                      <ChevronsUpDown />
                    </div>
                  </PopoverTrigger>
                  <PopoverContent className="p-0" align="center">
                    <Command>
                      <CommandInput
                        placeholder="Поиск категории"
                        className="placeholder:text-placeholder"
                      />
                      <CommandEmpty>Категории не найдены</CommandEmpty>
                      <CommandGroup>
                        <CommandItem
                          value=""
                          onSelect={() => field.onChange("")}
                          className="cursor-pointer hover:bg-background"
                        >
                          <Check
                            className={cn(
                              !field.value ? "opacity-100" : "opacity-0",
                            )}
                          />
                          Без родительской категории
                        </CommandItem>
                        {categories.map((cat) => (
                          <CommandItem
                            key={cat.id}
                            value={cat.name}
                            onSelect={() => field.onChange(cat.id.toString())}
                            className="cursor-pointer hover:bg-background"
                          >
                            <Check
                              className={cn(
                                field.value === cat.id.toString()
                                  ? "opacity-100"
                                  : "opacity-0",
                              )}
                            />
                            {`${cat.name} (${cat.shortName})`}
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </Command>
                  </PopoverContent>
                </Popover>
              )}
            />
          </Field>
        )}
      </FieldGroup>
      <Button
        className="w-full disabled:opacity-40 hover:bg-accent"
        variant="secondary"
        type="submit"
        disabled={
          isLoadingCategory ||
          !isValid ||
          error !== null ||
          measuresError !== null
        }
      >
        Создать
      </Button>
      {error && (
        <ErrorLabel
          message={error.message}
          onClearError={() => {
            clearError();
            refreshList();
          }}
        />
      )}
    </form>
  );
}
