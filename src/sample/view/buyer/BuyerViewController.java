package sample.view.buyer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import sample.database.DB;
import sample.model.Category;
import sample.model.Composition;
import sample.model.Product;
import sample.view.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuyerViewController extends Controller {
    @FXML private ListView<Object> elementsListView;

    @FXML private Text titleText;
    @FXML private ImageView productImageView;
    @FXML private Text descriptionText;
    @FXML private Text ingredientsText;
    @FXML private VBox nutrientsVBox;

    private ArrayList<Category> categories;
    private HashMap<Integer, List<Product>> productsByCategory;
    private Product selectedProduct;
    private final ObservableList<Object> listItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        categories = new ArrayList<>();
        productsByCategory = new HashMap<>();
        elementsListView.setItems(listItems);
        elementsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Object element, boolean empty) {
                super.updateItem(element, empty);
                if (empty || element == null) setText(null);
                else {
                    if (element instanceof Category) setText(((Category) element).getName());
                    else {
                        if (((Product) element).getId() == -1) setText("<<<");
                        else setText(((Product) element).getTitle());
                    }
                }
            }
        });
        elementsListView.setOnMouseClicked(event -> {
            Object o = elementsListView.getSelectionModel().getSelectedItem();
            if (o != null) {
                if (o instanceof Category) {
                    changeElementsListViewToProducts(((Category) o).getId());
                } else {
                    handleOnProductClicked(((Product) o));
                }
            }
        });
        disOrEnableElementsListView();
        fetchCategoriesAndSetToList();
    }

    private void fetchCategoriesAndSetToList() {
        DB.categoryRepo.getAllCategories(result -> {
            categories.addAll(result);
            listItems.clear();
            listItems.addAll(categories);
            disOrEnableElementsListView();
        }, System.out::println);
    }

    private void handleOnProductClicked(Product product) {
        if (product == selectedProduct) return;
        disOrEnableElementsListView();
        if (product.getId() == -1) {
            selectedProduct = null;
            listItems.clear();
            listItems.addAll(categories);
            disOrEnableElementsListView();
        } else {
            selectedProduct = product;
            // show product details
            nutrientsVBox.getChildren().clear();
            inflateProductNutrients(selectedProduct.getId());
            titleText.setText(selectedProduct.getTitle());
            try {
                Image img = new Image(selectedProduct.getPicture());
                productImageView.setImage(img);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getCause().getMessage());
            }
            String description = selectedProduct.getDescription();
            if (description.length() > 365) descriptionText.setText(description.substring(0, 365) + "...");
            else descriptionText.setText(description);
            ingredientsText.setText(selectedProduct.getIngredients());
            disOrEnableElementsListView();
        }
    }

    private void inflateProductNutrients(Integer productId) {
        DB.compositionRepo.getCompositionsByProductId(productId, result -> {
            addRowToNutrients("Nährstoffname", "Pro 100g", "Pro Portion");
            result.forEach(composition -> {
                addRowToNutrients(composition.getNutrient() + " " + composition.getUnit(),
                        composition.getPro_100().toString(), composition.getPro_por().toString());
            });
        }, System.out::println);
    }

    private void addRowToNutrients(String nutrient, String pro_100, String pro_por) {
        Text nutrientText = new Text();
        nutrientText.setWrappingWidth(226);
        nutrientText.setText(nutrient);
        Text pro_100Text = new Text();
        pro_100Text.setWrappingWidth(100);
        pro_100Text.setTextAlignment(TextAlignment.CENTER);
        pro_100Text.setText(pro_100);
        Text pro_porText = new Text();
        pro_porText.setTextAlignment(TextAlignment.CENTER);
        pro_porText.setWrappingWidth(100);
        pro_porText.setText(pro_por);
        if (nutrient.equals("Nährstoffname")) {
            nutrientText.setStyle("-fx-font-weight: bold");
            pro_100Text.setStyle("-fx-font-weight: bold");
            pro_porText.setStyle("-fx-font-weight: bold");
        }
        HBox row = new HBox();
        row.getChildren().add(nutrientText);
        row.getChildren().add(pro_100Text);
        row.getChildren().add(pro_porText);
        nutrientsVBox.getChildren().add(row);
    }

    private void changeElementsListViewToProducts(Integer categoryId) {
        disOrEnableElementsListView();
        listItems.clear();
        listItems.add(new Product(-1, "", "", "", ""));
        if (productsByCategory.containsKey(categoryId)) {
            listItems.addAll(productsByCategory.get(categoryId));
            disOrEnableElementsListView();
        } else {
            DB.productRepo.getProductsByCategoryId(categoryId, result -> {
                productsByCategory.put(categoryId, result);
                listItems.addAll(productsByCategory.get(categoryId));
                disOrEnableElementsListView();
            }, error -> {
                System.out.println(error);
                disOrEnableElementsListView();
            });
        }
    }

    private void disOrEnableElementsListView() {
        this.elementsListView.setDisable(!this.elementsListView.isDisabled());
    }

    @FXML
    public void goToCart() {
        DB.testRepo.getLoginData(System.out::println, System.out::println);
    }

    @FXML
    public void goToPayment() {
        DB.testRepo.getLoginData(System.out::println, System.out::println);
    }
}
