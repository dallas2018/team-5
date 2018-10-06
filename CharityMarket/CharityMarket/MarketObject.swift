//
//  MarketObject.swift
//  CharityMarket
//
//  Created by Zak Wegweiser on 10/5/18.
//  Copyright © 2018 Zak Wegweiser. All rights reserved.
//

import Foundation

class MarketObject : NSObject {
    var imageURL : String
    var name : String
    var price : String
    var tags : String
    var brand : String
    var descr : String
    var seller_uid : String
    var seller_charity : String
    
    init(imageURL : String, name : String, price : String, tags : String,
         brand : String, description: String, seller_uid : String, seller_charity : String) {
        self.imageURL = imageURL
        self.name = name
        self.price = price
        self.tags = tags
        self.brand = brand
        self.descr = description
        self.seller_uid = seller_uid
        self.seller_charity = seller_charity
        super.init()
    }
}
