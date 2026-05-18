"use client";

import { useForm } from "react-hook-form";
import { Button } from "../ui/button";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { useStore } from "@/lib/store/store";
import ErrorLabel from "../ErrorLabel/ErrorLabel";

export default function AddEnumeration() {
  const {
    register,
    handleSubmit,
    formState: { isValid, errors },
    reset,
  } = useForm({
    defaultValues: {
      name: "",
      shortName: "",
    },
  });
  const {
    enumerations: {
      errors: { creatingEnumError: error },
      clearError,
      isLoadingEnums,
      fetchEnumerations,
      createEnumeration,
    },
  } = useStore();

  const onSubmit = async (data: { name: string; shortName: string }) => {
    console.log(
      `Submit emited with -- name: ${data.name}, shortName: ${data.shortName}`,
    );
    await createEnumeration(data);
    await fetchEnumerations();
    reset();
  };

  const onReset = () => {
    clearError("creatingEnumError");
    reset({ name: "", shortName: "" });
  };

  return (
    <form
      className="flex flex-col gap-2 bg-dimmedblue rounded-md text-background p-2"
      onSubmit={handleSubmit(onSubmit)}
    >
      <FieldTitle className="text-md font-bold">
        Создание перечисления
      </FieldTitle>
      <FieldGroup>
        <Field>
          <FieldLabel htmlFor="new-enum-name">Название</FieldLabel>
          <Input
            id="new-enum-name"
            placeholder="Что?"
            type="text"
            required
            className="bg-background text-foreground placeholder:text-gray-400"
            {...register("name", { required: true })}
          />
          {errors.name && (
            <span className="text-accent text-xs">
              {errors.name.type === "required"
                ? "Обязательное поле"
                : "Минимум 1 символ"}
            </span>
          )}
        </Field>
        <Field>
          <FieldLabel htmlFor="new-enum-shortName">
            Короткое название
          </FieldLabel>
          <Input
            id="new-enum-shortName"
            type="text"
            required
            className="bg-background text-foreground placeholder:text-gray-400"
            placeholder="Коротко"
            {...register("shortName", { required: true })}
          />
          {errors.shortName && (
            <span className="text-accent text-xs">
              {errors.shortName.type === "required"
                ? "Обязательное поле"
                : "Минимум 1 символ"}
            </span>
          )}
        </Field>
      </FieldGroup>
      <Button
        type="submit"
        variant="secondary"
        className="hover:bg-accent cursor-pointer"
        disabled={isLoadingEnums || !isValid}
      >
        Создать
      </Button>
      {error && <ErrorLabel message={error.message} onClearError={onReset} />}
    </form>
  );
}
