//
//  ProductViewController.swift
//  CharityMarket
//
//  Created by Zak Wegweiser on 10/6/18.
//  Copyright © 2018 Zak Wegweiser. All rights reserved.
//

import UIKit

class ProductViewController : UIViewController
{
    var marketObject : MarketObject!
    @IBOutlet var productImageView : UIImageView!
    @IBOutlet var nameLabel : UILabel!
    @IBOutlet var brandLabel : UILabel!
    @IBOutlet var priceLabel : UILabel!
    @IBOutlet var descriptionTextView : UITextView!
    @IBOutlet var purchaseButton : UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        productImageView.downloadImageFrom(link: marketObject.imageURL, contentMode: .scaleAspectFit)
        nameLabel.text = marketObject.name
        brandLabel.text = marketObject.brand
        priceLabel.text = "$\(marketObject.price).00"
        descriptionTextView.text = marketObject.descr
        purchaseButton.setTitle("Purchase for '" + marketObject.seller_charity + "'", for: .normal)
        
        self.title = marketObject.name
    }
    
    @IBAction func purchase()
    {
        let purchaseVC = self.storyboard?.instantiateViewController(withIdentifier: "PurchaseViewController") as! PurchaseViewController
        purchaseVC.marketObject = marketObject
        self.navigationController?.pushViewController(purchaseVC, animated: true)
    }
    
    @IBAction func cancelPressed()
    {
        self.dismiss(animated: true, completion: nil)
    }
}
