"use client";

import { useForm } from "react-hook-form";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { useStore } from "@/lib/store/store";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import CreateProductDto from "@/lib/dto/createProductDto";

interface AddProductFormSchema {
  name: string;
  shortName: string;
  baseClassId: number | null;
}

export default function AddProduct() {
  const {
    register,
    formState: { errors, isValid },
    handleSubmit,
    reset,
  } = useForm<AddProductFormSchema>();
  const {
    products: {
      errors: { modifyingError },
      clearError,
      isModifyLoading,
      createProduct,
      fetchProducts,
    },
  } = useStore();

  const onSubmit = async (data: AddProductFormSchema) => {
    if (!data.baseClassId) return;
    const dto: CreateProductDto = {
      name: data.name,
      shortName: data.shortName,
      classId: data.baseClassId,
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
        <Field>
          <FieldLabel htmlFor="new-product-baseClass">
            Идентификатор базового класса
          </FieldLabel>
          <Input
            id="new-product-baseClass"
            type="number"
            min={1}
            className="bg-background text-foreground placeholder:text-gray-400"
            placeholder="ID баз. класса"
            {...register("baseClassId", {
              required: "Обязательное поле",
              setValueAs: (value: string): number | null => {
                const parsed = Number(value);
                return !isNaN(parsed) ? parsed : null;
              },
              validate: async (value: number | null) => {
                if (value && value < 1)
                  return "Значение должно быть положительно";
                return true;
              },
            })}
          />
          {errors.baseClassId && (
            <span className="text-accent text-xs">
              {errors.baseClassId.message}
            </span>
          )}
        </Field>
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
