"use client";

import { useStore } from "@/lib/store/store";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { useEffect } from "react";
import { Item, ItemActions, ItemGroup, ItemTitle } from "../ui/item";
import Parameter from "@/types/parameter";
import Loader from "../Loader/Loader";
import { Button } from "../ui/button";
import { Edit, Trash } from "lucide-react";

import EditParamDialog from "../dialog/EditParamDialog";

export default function ParametersList() {
  const {
    parameters: {
      items,
      clearItems,
      fetchParameters,
      error,
      clearError,
      isLoading,
      deleteParameter,
      dialog: { setEditOpen, isEditOpen, editedParam },
    },
  } = useStore();

  useEffect(() => {
    fetchParameters();
    return () => {
      clearItems("all");
    };
  }, []);

  if (error) {
    return (
      <div className="h-full flex justify-center items-center">
        <ErrorLabel
          message={error.message}
          onClearError={() => {
            clearError();
            fetchParameters();
          }}
        />
      </div>
    );
  }

  return (
    <div className="h-full">
      <h3 className="p-1 text-xl">Список параметров</h3>
      <div className="flex justify-center max-h-100 items-center p-1 overflow-y-auto">
        {isLoading ? (
          <Item className="h-full justify-center items-center">
            <Loader />
          </Item>
        ) : items.length ? (
          <div className="w-full">
            <ItemGroup>
              {items.map((p) => (
                <ParameterItem
                  key={p.id}
                  data={p}
                  handleDelete={async () => {
                    await deleteParameter(p.id);
                    fetchParameters();
                  }}
                />
              ))}
            </ItemGroup>
          </div>
        ) : (
          <div className="h-full flex justify-center items-center">
            <p>Пусто...</p>
          </div>
        )}
      </div>
      <EditParamDialog
        isOpen={isEditOpen}
        onOpenChange={setEditOpen}
        editedParam={editedParam}
      />
    </div>
  );
}

interface ParameterItemProps {
  data: Parameter;
  handleDelete: () => Promise<void>;
}

function ParameterItem({ data, handleDelete }: ParameterItemProps) {
  const {
    parameters: {
      isLoading,
      dialog: { setEditOpen, setEditedParam },
    },
  } = useStore();

  return (
    <Item className="justify-between border-2 border-dimmedblue rounded-md bg-gray-200">
      <ItemTitle>{`${data.id}: ${data.name} (${data.shortName})`}</ItemTitle>
      <ItemActions>
        <Button
          variant="secondary"
          type="button"
          disabled={isLoading}
          className="hover:bg-accent"
          onClick={() => {
            setEditOpen(true);
            setEditedParam(data);
          }}
        >
          <Edit />
        </Button>
        <Button
          variant="secondary"
          className="hover:bg-accent"
          type="button"
          onClick={handleDelete}
          disabled={isLoading}
        >
          <Trash />
        </Button>
      </ItemActions>
    </Item>
  );
}
