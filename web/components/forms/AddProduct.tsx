"use client";

import { Controller, useForm } from "react-hook-form";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { useStore } from "@/lib/store/store";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import CreateProductDto from "@/lib/dto/createProductDto";
import { Popover, PopoverContent, PopoverTrigger } from "../ui/popover";
import { cn } from "@/lib/utils";
import { useEffect } from "react";
import { Check, ChevronsUpDown } from "lucide-react";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
} from "../ui/command";
import { Item } from "../ui/item";
import Loader from "../Loader/Loader";

interface AddProductFormSchema {
  name: string;
  shortName: string;
  baseClassId: string;
}

export default function AddProduct() {
  const {
    register,
    formState: { errors, isValid },
    handleSubmit,
    reset,
    control,
  } = useForm<AddProductFormSchema>({ mode: "onChange" });
  const {
    products: {
      errors: { modifyingError },
      clearError,
      isModifyLoading,
      createProduct,
      fetchProducts,
    },
    categories: {
      items: categories,
      isLoading: isLoadingCategory,
      fetchCategories,
    },
  } = useStore();

  useEffect(() => {
    fetchCategories("all");
  }, [fetchCategories]);

  const onSubmit = async (data: AddProductFormSchema) => {
    if (!data.baseClassId || isNaN(Number(data.baseClassId))) return;
    const dto: CreateProductDto = {
      name: data.name,
      shortName: data.shortName,
      classId: Number(data.baseClassId),
    };
    await createProduct(dto);
    reset();
    fetchProducts();
  };

  return (
    <form
      className="flex flex-col gap-2 bg-dimmedblue rounded-md text-background p-2"
      onSubmit={handleSubmit(onSubmit)}
    >
      <FieldTitle className="text-md font-bold">Добавить изделие</FieldTitle>
      <FieldGroup>
        <Field>
          <FieldLabel htmlFor="new-product-name">Название</FieldLabel>
          <Input
            id="new-product-name"
            type="text"
            className="bg-background text-foreground placeholder:text-gray-400"
            placeholder="Название"
            {...register("name", { required: true })}
          />
          {errors.name && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
        <Field>
          <FieldLabel htmlFor="new-product-shortName">
            Короткое название
          </FieldLabel>
          <Input
            id="new-product-shortName"
            type="text"
            className="bg-background text-foreground placeholder:text-gray-400"
            placeholder="Короткое название"
            {...register("shortName", { required: true })}
          />
          {errors.shortName && (
            <span className="text-accent text-xs">Обязательное поле</span>
          )}
        </Field>
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
              rules={{
                validate: async (value) => {
                  if (!value) return "Обязательное поле";
                  return true;
                },
              }}
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
            {errors.baseClassId && (
              <span className="text-accent text-xs">Обязательное поле</span>
            )}
          </Field>
        )}
      </FieldGroup>
      <Button
        type="submit"
        className="hover:bg-accent"
        variant="secondary"
        disabled={!isValid || isModifyLoading}
      >
        Создать
      </Button>
      {modifyingError && (
        <ErrorLabel
          message={modifyingError.message}
          onClearError={() => {
            clearError("modifyingError");
          }}
        />
      )}
    </form>
  );
}
