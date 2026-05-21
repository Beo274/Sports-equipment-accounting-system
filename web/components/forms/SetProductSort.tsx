"use client";

import { useStore } from "@/lib/store/store";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { useEffect, useState } from "react";

export default function SetProductSort() {
  const {
    products: {
      fetchType,
      setFetchType,
      classId,
      setClassId,
      isFetchLoading,
      fetchProducts,
    },
  } = useStore();
  const [newClassId, setNewClassId] = useState("");

  useEffect(() => {
    const parsed = Number(newClassId);
    if (!isNaN(parsed) && parsed > 0) {
      setClassId(parsed);
    } else {
      setClassId(undefined);
    }
  }, [newClassId, setClassId]);

  useEffect(() => {
    fetchProducts();
  }, [classId]);

  return (
    <RadioGroup
      value={fetchType}
      onValueChange={setFetchType}
      className="grid grid-cols-2 p-1"
    >
      <div className="flex items-center justify-between gap-4 rounded-lg border p-4 bg-dimmedblue text-background">
        <Field>
          <FieldLabel
            htmlFor="all-products"
            className="text-base flex flex-col items-start cursor-pointer"
          >
            <FieldTitle>Все изделия</FieldTitle>
          </FieldLabel>
        </Field>
        <Field className="flex-row">
          <FieldLabel htmlFor="sort-classId">ID категории</FieldLabel>
          <Input
            id="sort-classId"
            className="bg-background text-foreground placeholder:text-gray-400"
            placeholder="ID"
            type="number"
            min={1}
            value={newClassId}
            onChange={(e) => setNewClassId(e.target.value)}
          />
        </Field>
        <RadioGroupItem
          value="all"
          id="all-products"
          className="shrink-0 cursor-pointer hover:scale-140 transition-transform"
          disabled={isFetchLoading}
        />
      </div>
      <div className="flex items-center justify-between gap-4 rounded-lg border p-4 bg-dimmedblue text-background">
        <Field>
          <FieldLabel
            htmlFor="products-with-params"
            className="text-base flex flex-col items-start cursor-pointer"
          >
            <FieldTitle>По параметрам</FieldTitle>
          </FieldLabel>
        </Field>
        <RadioGroupItem
          value="with-params"
          id="products-with-params"
          className="shrink-0 cursor-pointer hover:scale-140 transition-transform"
          disabled={isFetchLoading}
        />
      </div>
    </RadioGroup>
  );
}
