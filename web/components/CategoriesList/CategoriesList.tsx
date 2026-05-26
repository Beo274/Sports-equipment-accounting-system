"use client";

import {
  Item,
  ItemActions,
  ItemContent,
  ItemDescription,
  ItemGroup,
  ItemHeader,
  ItemTitle,
} from "../ui/item";
import { useEffect, useState } from "react";
import { Button } from "../ui/button";
import {
  EditIcon,
  Grid2x2Plus,
  TableProperties,
  TrashIcon,
} from "lucide-react";
import { useStore } from "@/lib/store/store";
import Loader from "../Loader/Loader";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import AddParameterDialog from "../dialog/AddParameterDialog";
import ListParametersDialog from "../dialog/ListParametersDialog";
import EditCategoryDialog from "../dialog/EditCategoryDialog";
import { Category } from "@/types/category";
import { ScrollArea, ScrollBar } from "../ui/scroll-area";

export function CategoriesList() {
  const {
    categories: {
      items,
      isLoading,
      listType,
      deleteCategory,
      refreshList,
      error,
      clearError,
      dialog: { editingCategory },
      clearItems,
    },
  } = useStore();
  const [isAddParamOpen, setAddParamOpen] = useState(false);
  const [isListParamsOpen, setListParamsOpen] = useState(false);
  const [isEditOpen, setEditOpen] = useState(false);

  useEffect(() => {
    refreshList();
  }, [listType, refreshList]);

  useEffect(() => {
    return () => {
      clearItems();
    };
  }, []);

  const onDeleteCategory = (classId: number) => {
    return async () => {
      await deleteCategory(classId);
      await refreshList();
    };
  };

  if (error) {
    return (
      <div className="h-full flex justify-center items-center">
        <ErrorLabel
          message={error.message}
          onClearError={() => {
            clearError();
            refreshList();
          }}
        />
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col gap-2">
      <h3 className="p-1 text-xl">Вывод категорий</h3>
      <div className="col-start-1 row-start-1 flex-1 min-h-0">
        {isLoading ? (
          <Item className="h-full justify-center items-center">
            <Loader />
          </Item>
        ) : items.length ? (
          <ScrollArea className="h-full max-h-100 rounded-md border">
            <ItemGroup className="flex flex-col p-2">
              {items.map((c) => (
                <CategoriesListItem
                  key={c.id}
                  category={c}
                  handleDelete={onDeleteCategory(c.id)}
                  openAddParam={() => setAddParamOpen(true)}
                  openListParam={() => setListParamsOpen(true)}
                  openEdit={() => setEditOpen(true)}
                />
              ))}
            </ItemGroup>
          </ScrollArea>
        ) : (
          <div className="h-full flex justify-center items-center">
            <p>Пусто...</p>
          </div>
        )}
      </div>
      <AddParameterDialog
        open={isAddParamOpen}
        onOpenChange={setAddParamOpen}
        editingEntity={{
          paramFor: "class",
          entity: editingCategory,
        }}
      />
      <ListParametersDialog
        open={isListParamsOpen}
        onOpenChange={setListParamsOpen}
        dialogEntity={{
          paramFor: "class",
          entity: editingCategory,
        }}
      />
      <EditCategoryDialog
        open={isEditOpen}
        onOpenChange={setEditOpen}
        category={editingCategory}
      />
    </div>
  );
}

interface CategoriesListItemProps {
  category: Category;

  handleDelete: () => Promise<void>;
  openAddParam: () => void;
  openListParam: () => void;
  openEdit: () => void;
}

export function CategoriesListItem({
  category,
  handleDelete,
  openAddParam,
  openEdit,
  openListParam,
}: CategoriesListItemProps) {
  const {
    categories: {
      isLoading,
      dialog: { setEditingCategory },
    },
  } = useStore();

  return (
    <Item className="grid grid-cols-2 items-start gap-1 border border-accent rounded-lg max-w-sm p-0 hover:shadow-lg hover:shadow-foreground transition-all">
      <ItemHeader className="col-span-2 p-0 border border-accent rounded-t-md bg-accent">
        <ItemTitle className="p-1 w-full font-bold">{`${category.id}: ${category.name} (${category.shortName})`}</ItemTitle>
      </ItemHeader>
      <ItemContent>
        <ItemDescription className="col-start-1 px-2">
          {category.baseClassId
            ? `ID базовой категории: ${category.baseClassId}`
            : "Корневая категория"}
        </ItemDescription>
        <ItemActions className="col-start-2 justify-self-end p-1">
          <Button
            onClick={() => {
              openEdit();
              setEditingCategory(category);
            }}
            variant={"ghost"}
            className="hover:bg-accent"
            disabled={isLoading}
            title="Редактировать"
          >
            <EditIcon />
          </Button>
          <Button
            variant="ghost"
            className="hover:bg-accent"
            disabled={isLoading}
            title="Добавить параметр"
            onClick={() => {
              openAddParam();
              setEditingCategory(category);
            }}
          >
            <Grid2x2Plus />
          </Button>
          <Button
            variant="ghost"
            className="hover:bg-accent"
            disabled={isLoading}
            title="Посмотреть параметры"
            onClick={() => {
              openListParam();
              setEditingCategory(category);
            }}
          >
            <TableProperties />
          </Button>
          <Button
            onClick={handleDelete}
            variant={"ghost"}
            className="hover:bg-accent"
            disabled={isLoading}
            title="Удалить"
          >
            <TrashIcon />
          </Button>
        </ItemActions>
      </ItemContent>
    </Item>
  );
}
