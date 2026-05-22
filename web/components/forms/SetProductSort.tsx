"use client";

import { useStore } from "@/lib/store/store";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { useEffect, useState } from "react";
import Parameter, { NullParameterId } from "@/types/parameter";
import { Checkbox } from "../ui/checkbox";
import Loader from "../Loader/Loader";
import {
  Command,
  CommandDialog,
  CommandGroup,
  CommandList,
} from "../ui/command";
import { Button } from "../ui/button";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";

export default function SetProductSort() {
  const {
    products: {
      fetchType,
      setFetchType,
      setClassId,
      isFetchLoading,
      fetchProducts,
      paramsIds,
      setParamsIds,
      rangeMaxVal,
      rangeMinVal,
      setRangeMaxVal,
      setRangeMinVal,
      rangeParamId,
      setRangeParamId,
    },
    parameters: {
      items: params,
      isLoading: isLoadingParams,
      error,
      clearError,
      fetchParameters,
    },
  } = useStore();
  const [newClassId, setNewClassId] = useState("");
  const [isParamFilterOpen, setParamFilterOpen] = useState(false);
  useEffect(() => {
    const parsed = Number(newClassId);
    if (!isNaN(parsed) && parsed > 0) {
      setClassId(parsed);
    } else {
      setClassId(undefined);
    }
  }, [newClassId, setClassId]);

  useEffect(() => {
    fetchParameters();
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  return (
    <RadioGroup
      value={fetchType}
      onValueChange={setFetchType}
      className="h-full flex flex-col justify-between"
    >
      <div className="flex gap-4 rounded-lg border p-4 bg-dimmedblue text-background">
        <Field className="flex-row">
          <FieldLabel
            htmlFor="all-products"
            className="text-base flex cursor-pointer"
          >
            <FieldTitle>Все изделия</FieldTitle>
          </FieldLabel>
        </Field>
        <Field className="flex-row items-center">
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
          className="col-start-2 row-span-2 self-center shrink-0 cursor-pointer hover:scale-140 transition-transform"
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
        <Button
          className="hover:bg-accent"
          variant="secondary"
          title="Выбрать параметры"
          onClick={() => setParamFilterOpen(true)}
        >
          Параметры
        </Button>
        <RadioGroupItem
          value="with-params"
          id="products-with-params"
          className="shrink-0 cursor-pointer hover:scale-140 transition-transform"
          disabled={isFetchLoading}
        />
      </div>
      <div className="flex items-center justify-between gap-4 rounded-lg border p-4 bg-dimmedblue text-background">
        <Field>
          <FieldLabel
            htmlFor="products-with-param-range"
            className="text-base flex flex-col items-start cursor-pointer"
          >
            <FieldTitle>По числовому диапазону параметра</FieldTitle>
          </FieldLabel>
          <Select
            value={rangeParamId}
            onValueChange={(value: string | null) => {
              if (value) setRangeParamId(value);
              else setRangeParamId(NullParameterId);
            }}
            defaultValue={NullParameterId}
            disabled={isLoadingParams}
          >
            <SelectTrigger className="bg-background text-foreground">
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
        </Field>
        <FieldGroup>
          <Field>
            <Input
              id="min-val"
              className="bg-background text-foreground placeholder:text-gray-400"
              placeholder="От"
              value={rangeMinVal}
              onChange={(e) => setRangeMinVal(e.target.value)}
              type="number"
            />
          </Field>
          <Field>
            <Input
              id="max-val"
              className="bg-background text-foreground placeholder:text-gray-400"
              placeholder="До"
              value={rangeMaxVal}
              onChange={(e) => setRangeMaxVal(e.target.value)}
              type="number"
            />
          </Field>
        </FieldGroup>
        <RadioGroupItem
          value="within-range"
          id="products-with-param-range"
          className="shrink-0 cursor-pointer hover:scale-140 transition-transform"
          disabled={isFetchLoading}
        />
      </div>
      <CommandDialog open={isParamFilterOpen} onOpenChange={setParamFilterOpen}>
        <Command>
          <CommandList className="overflow-y-auto">
            <CommandGroup heading="Список параметров">
              {error && (
                <ErrorLabel
                  message={error.message}
                  onClearError={async () => {
                    clearError();
                    fetchParameters();
                  }}
                />
              )}
              {isLoadingParams ? (
                <Loader />
              ) : (
                <div className="overflow-y-auto">
                  <ParamsCheckboxList
                    params={params}
                    paramsIds={paramsIds}
                    addParamId={(id: number) => {
                      setParamsIds((prev) => new Set(prev).add(id));
                    }}
                    removeParamId={(id: number) => {
                      setParamsIds((prev) => {
                        const next = new Set(prev);
                        next.delete(id);
                        return next;
                      });
                    }}
                  />
                </div>
              )}
            </CommandGroup>
          </CommandList>
        </Command>
      </CommandDialog>
    </RadioGroup>
  );
}

interface ParamsCheckboxListProps {
  params: ReadonlyArray<Parameter>;
  paramsIds: ReadonlySet<number>;
  addParamId: (id: number) => void;
  removeParamId: (id: number) => void;
}

function ParamsCheckboxList({
  params,
  paramsIds,
  addParamId,
  removeParamId,
}: ParamsCheckboxListProps) {
  return (
    <FieldGroup className="px-3 py-3">
      {params.map((p) => (
        <Field key={p.id} className="w-full flex-row">
          <FieldLabel>{`${p.name} (${p.shortName})`}</FieldLabel>
          <Checkbox
            id={`cb_${p.id}`}
            name={`cb_${p.id}`}
            className="max-w-4 cursor-pointer"
            checked={paramsIds.has(p.id)}
            onCheckedChange={(checked: boolean) => {
              if (checked) addParamId(p.id);
              else removeParamId(p.id);
            }}
          />
        </Field>
      ))}
    </FieldGroup>
  );
}
