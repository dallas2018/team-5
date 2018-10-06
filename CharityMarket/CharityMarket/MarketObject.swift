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
    var category : String
    var brand : String
    
    init(imageURL : String, name : String, price : String, category : String, brand : String) {
        self.imageURL = imageURL
        self.name = name
        self.price = price
        self.category = category
        self.brand = brand
        super.init()
    }
}
